package team.creative.creativecore.common.gui.controls.tree;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiButtonHoldSlim;
import team.creative.creativecore.common.gui.controls.simple.GuiCheckBox;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.type.itr.FilterIterator;

import java.util.ArrayList;
import java.util.List;

public class GuiTreeItem extends GuiParent {
    
    public final GuiTree tree;
    private GuiTreeItem parentItem;
    private final List<GuiTreeItem> items = new ArrayList<>();
    private int level = 0;
    private boolean open = true;
    private boolean selected = false;
    private GuiCheckBox checkbox;
    private GuiLabel label;
    private GuiButton button;
    private ItemClickState state = null;
    protected boolean moving = false;
    
    public GuiTreeItem(String name, GuiTree tree) {
        super(name);
        this.tree = tree;
        if (tree.hasCheckboxes())
            add(getOrCreateCheckbox());
        add(label = new GuiLabel("title"));
        flow = GuiFlow.STACK_X;
        spacing = 5;
    }
    
    public GuiTreeItem setTranslate(String translate) {
        label.setTranslate(translate);
        return this;
    }
    
    public GuiTreeItem setTitle(Component component) {
        label.setTitle(component);
        return this;
    }
    
    public GuiTreeItem setTitle(List<Component> components) {
        label.setTitle(components);
        return this;
    }
    
    public void resetCheckboxPartial() {
        if (checkbox != null)
            checkbox.partial = false;
    }
    
    protected GuiCheckBox getOrCreateCheckbox() {
        if (checkbox != null)
            return checkbox;
        return checkbox = new GuiCheckBox("box", true).consumeChanged(x -> {
            if (parentItem != null && tree.hasCheckboxesPartial())
                parentItem.childCheckedChanged(x);
            setChecked(x);
        });
    }
    
    public boolean isMoving() {
        return moving;
    }
    
    public void setMoving(boolean moving) {
        this.moving = moving;
    }
    
    public boolean opened() {
        return open;
    }
    
    public void toggle() {
        if (button != null) {
            open = !open;
            button.setTitle(Component.literal(open ? "-" : "+"));
            tree.updateTree();
        }
    }
    
    public boolean isAtLeastPartiallyChecked() {
        return checkbox != null && (checkbox.value || checkbox.partial);
    }
    
    public boolean isChecked() {
        return checkbox != null && checkbox.value;
    }
    
    protected void setChecked(boolean value) {
        if (checkbox != null) {
            checkbox.value = value;
            checkbox.partial = false;
        }
        if (tree.hasCheckboxesPartial())
            for (GuiTreeItem item : items)
                item.setChecked(value);
    }
    
    protected void childCheckedChanged(boolean value) {
        if (checkbox == null)
            return;
        if (checkbox.value)
            return;
        if (value) {
            if (checkbox.partial)
                return;
            checkbox.partial = true;
            if (parentItem != null)
                parentItem.childCheckedChanged(true);
        } else {
            if (!checkbox.partial)
                return;
            
            for (GuiTreeItem item : items)
                if (item.isAtLeastPartiallyChecked())
                    return;
                
            checkbox.partial = false;
            if (parentItem != null)
                parentItem.childCheckedChanged(false);
        }
        
    }
    
    protected void updateControls() {
        if (tree.hasCheckboxes() == (checkbox != null) && items.isEmpty() == (button == null))
            return;

        clear();
        if (items.isEmpty()) {
            button = null;
        } else {
            add(button = (GuiButton) new GuiButtonHoldSlim("expand", x -> toggle()).setTitle(Component.literal("-")));
        }
        if (tree.hasCheckboxes())
            add(getOrCreateCheckbox());
        else if (checkbox != null)
            checkbox = null;
        add(label);
    }
    
    public GuiTreeItem getParentItem() {
        return parentItem;
    }
    
    public void clearItems() {
        for (GuiTreeItem item : items) {
            item.parentItem = null;
            item.removed();
            updateControls();
        }
        items.clear();
    }
    
    public boolean removeItem(GuiTreeItem item) {
        if (items.remove(item)) {
            item.parentItem = null;
            item.removed();
            updateControls();
            return true;
        }
        return false;
    }
    
    public void removed() {}
    
    public void insertItemAfter(GuiTreeItem before, GuiTreeItem item) {
        insertItemAfter(indexOf(before), item);
    }
    
    public void insertItemAfter(int index, GuiTreeItem item) {
        item.parentItem = this;
        item.updateLevel();
        items.add(index + 1, item);
        item.added();
        updateControls();
    }
    
    public void insertItem(GuiTreeItem before, GuiTreeItem item) {
        insertItem(indexOf(before), item);
    }
    
    public void insertItem(int index, GuiTreeItem item) {
        item.parentItem = this;
        item.updateLevel();
        items.add(index, item);
        item.added();
        updateControls();
    }
    
    public void addItem(GuiTreeItem item) {
        item.parentItem = this;
        item.updateLevel();
        items.add(item);
        item.added();
        updateControls();
    }
    
    public void added() {}
    
    protected void updateLevel() {
        level = parentItem.level + 1;
        for (GuiTreeItem item : items)
            item.updateLevel();
    }
    
    public int getLevel() {
        return level;
    }
    
    public Iterable<GuiTreeItem> items() {
        return items;
    }
    
    public Iterable<GuiTreeItem> itemsChecked() {
        return new FilterIterator<>(items, GuiTreeItem::isAtLeastPartiallyChecked);
    }
    
    public GuiTreeItem getItem(int index) {
        return items.get(index);
    }
    
    public int indexOf(GuiTreeItem item) {
        return items.indexOf(item);
    }
    
    public int itemsCount() {
        return items.size();
    }
    
    public boolean isChild(GuiTreeItem item) {
        for (GuiTreeItem child : items)
            if (child == item || child.isChild(item))
                return true;
        return false;
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        super.mouseMoved(rect, x, y);
        if (state == ItemClickState.CLICKED && !tree.isDragged() && !rect.inside(x, y)) {
            tree.startDrag(this);
            state = ItemClickState.DRAGGED;
        }
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        super.mouseReleased(rect, x, y, button);
        
        if (state == ItemClickState.CLICKED) {
            tree.select(this);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            state = null;
        } else if (state == ItemClickState.DRAGGED) {
            state = null;
            if (tree.endDrag())
                playSound(SoundEvents.UI_BUTTON_CLICK, 0.1F, 2F);
            
        }
        
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (super.mouseClicked(rect, x, y, button))
            return true;
        state = ItemClickState.CLICKED;
        return true;
    }
    
    @Override
    public boolean mouseDoubleClicked(Rect rect, double x, double y, int button) {
        toggle();
        tree.select(this);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    public boolean testForDoubleClick(Rect rect, double x, double y, int button) {
        return button == 0;
    }
    
    protected void updateColor() {
        if (selected)
            label.setDefaultColor(ColorUtils.YELLOW);
        else
            label.setDefaultColor(ColorUtils.WHITE);
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        if (state == ItemClickState.DRAGGED)
            return ControlFormatting.OUTLINE;
        return super.getControlFormatting();
    }
    
    public boolean selected() {
        return selected;
    }
    
    protected void select() {
        selected = true;
        updateColor();
    }
    
    protected void deselect() {
        selected = false;
        updateColor();
    }
    
    private static enum ItemClickState {
        
        CLICKED,
        DRAGGED;
        
    }
    
}
