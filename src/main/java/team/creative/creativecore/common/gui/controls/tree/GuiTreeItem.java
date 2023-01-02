package team.creative.creativecore.common.gui.controls.tree;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiButtonHoldSlim;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiTreeItem extends GuiParent {
    
    public final GuiTree tree;
    private GuiTreeItem parentItem;
    private List<GuiTreeItem> items = new ArrayList<>();
    private int level = 0;
    private boolean open = true;
    private boolean selected = false;
    private GuiLabel label;
    private GuiButton button;
    private ItemClickState state = null;
    
    public GuiTreeItem(String name, GuiTree tree) {
        super(name);
        this.tree = tree;
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
    
    protected void updateControls() {
        if (items.isEmpty() && button != null) {
            clear();
            button = null;
            add(label);
        } else if (!items.isEmpty() && button == null) {
            clear();
            add(button = (GuiButton) new GuiButtonHoldSlim("expand", x -> {
                toggle();
            }).setTitle(Component.literal("-")));
            add(label);
        }
    }
    
    public GuiTreeItem getParentItem() {
        return parentItem;
    }
    
    public boolean removeItem(GuiTreeItem item) {
        if (items.remove(item)) {
            item.parentItem = null;
            updateControls();
            return true;
        }
        return false;
    }
    
    public void insertItem(int index, GuiTreeItem item) {
        item.parentItem = this;
        item.updateLevel();
        items.add(index, item);
        updateControls();
    }
    
    public void addItem(GuiTreeItem item) {
        item.parentItem = this;
        item.updateLevel();
        items.add(item);
        updateControls();
    }
    
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
        } else if (state == ItemClickState.DRAGGED) {
            if (tree.endDrag())
                playSound(SoundEvents.UI_BUTTON_CLICK, 0.1F, 2F);
        }
        state = null;
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
    public boolean testForDoubleClick(Rect rect, double x, double y) {
        return true;
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
