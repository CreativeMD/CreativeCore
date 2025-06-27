package team.creative.creativecore.common.gui.control.tree;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3x2fStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.control.parent.GuiScrollXY;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.control.tree.GuiTreeDragPosition.ItemPosition;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.type.itr.NestedFunctionIterator;
import team.creative.creativecore.common.util.type.itr.TreeIterator;

public class GuiTree extends GuiScrollXY {
    
    private static final int levelSpacing = 10;
    private int lastWidth;
    private int lastHeight;
    
    private boolean searchbar = false;
    private boolean checkboxes = false;
    private boolean checkboxesPartial = false;
    protected boolean canDeselect = true;
    private final GuiTextfield search;
    private boolean visibleRoot = false;
    private final GuiTreeItem root;
    protected StyleDisplay line = new DisplayColor(ColorUtils.WHITE);
    protected StyleDisplay dragLine = new DisplayColor(ColorUtils.BLACK);
    protected StyleDisplay dragHover = new DisplayColor(ColorUtils.rgb(200, 150, 0));
    private int lineThickness;
    private int halfLineThickness;
    
    private GuiTreeItem selected = null;
    private GuiTreeItem dragged = null;
    private GuiTreeDragPosition lastDragPosition = null;
    
    public GuiTree(String name) {
        this(name, false);
    }
    
    public GuiTree(String name, boolean searchbar) {
        super(name, GuiFlow.STACK_Y);
        this.searchbar = searchbar;
        if (searchbar)
            search = new GuiTextfield(name);
        else
            search = null;
        this.root = new GuiTreeItem("root", this);
        spacing = 3;
        setLineThickness(1);
    }
    
    public GuiTree setCheckboxes(boolean checkboxes, boolean partial) {
        boolean changed = checkboxes != this.checkboxes || partial != this.checkboxesPartial;
        this.checkboxes = checkboxes;
        this.checkboxesPartial = partial;
        if (changed)
            for (GuiTreeItem item : allItems()) {
                if (!partial)
                    item.resetCheckboxPartial();
                item.updateControls();
            }
        return this;
    }
    
    public GuiTree keepSelected() {
        canDeselect = false;
        return this;
    }
    
    public boolean hasCheckboxes() {
        return checkboxes;
    }
    
    public boolean hasCheckboxesPartial() {
        return checkboxesPartial;
    }
    
    public Iterable<GuiTreeItem> allItems() {
        return new TreeIterator<>(root, x -> x.items().iterator());
    }
    
    public Iterable<GuiTreeItem> itemsChecked() {
        if (visibleRoot)
            return root.itemsChecked();
        return new NestedFunctionIterator<>(root.items(), GuiTreeItem::itemsChecked);
    }
    
    public GuiTreeItem selected() {
        return selected;
    }
    
    public GuiTreeItem getFirst() {
        if (visibleRoot)
            return root;
        else if (root.itemsCount() > 0)
            return root.getItem(0);
        return null;
    }
    
    public boolean selectFirst() {
        if (visibleRoot)
            select(root);
        else if (root.itemsCount() > 0)
            select(root.getItem(0));
        else {
            select(null);
            return false;
        }
        return true;
    }
    
    public void select(GuiTreeItem item) {
        if (selected != null)
            selected.deselect();
        GuiTreeItem old = selected;
        selected = item;
        if (item != null)
            item.select();
        raiseEvent(new GuiTreeSelectionChanged(this, old, item));
    }
    
    public void setLineThickness(int thickness) {
        lineThickness = thickness;
        halfLineThickness = thickness / 2;
    }
    
    public GuiTree setRootVisibility(boolean visible) {
        visibleRoot = visible;
        return this;
    }
    
    public GuiTreeItem root() {
        return root;
    }
    
    public void updateTree() {
        clear();
        if (searchbar)
            add(search);
        if (visibleRoot)
            add(root);
        if (!visibleRoot || root.opened())
            addItem(root);
        
        reflowTree();
    }
    
    public void reflowTree() {
        if (lastWidth != 0) {
            flowX(lastWidth, preferredWidth(lastWidth));
            flowY(lastWidth, lastHeight, preferredHeight(lastWidth, lastHeight));
        }
    }
    
    private void addItem(GuiTreeItem item) {
        for (GuiTreeItem subItem : item.items()) {
            add(subItem);
            if (subItem.opened())
                addItem(subItem);
        }
    }
    
    protected int offsetByLevel(int level) {
        if (visibleRoot)
            return levelSpacing * level;
        return levelSpacing * (level - 1);
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        int width = 0;
        for (GuiControl control : controls)
            if (control instanceof GuiTreeItem item)
                width = Math.max(width, offsetByLevel(item.getLevel()) + 1 + control.rect.getPreferredWidth(availableWidth));
            else
                width = Math.max(width, control.rect.getPreferredWidth(availableWidth));
        return width;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        for (GuiControl control : controls)
            if (control instanceof GuiTreeItem item)
                control.rect.setX(offsetByLevel(item.getLevel()) + 1);
        updateWidth();
        lastWidth = width;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        super.flowY(width, height, preferred);
        lastHeight = height;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        if (isDragged()) {
            pose.pushMatrix();
            pose.translate((float) (getContentOffset() + getOffsetX()), (float) (getContentOffset() + getOffsetY()));
            lastDragPosition = calculatePosition((int) (mouseX - realRect.minX - getContentOffset()), (int) (mouseY - realRect.minY - getContentOffset()));
            if (lastDragPosition != null) {
                if (lastDragPosition.position() == ItemPosition.IN)
                    dragHover.render(graphics, lastDragPosition.item().rect);
                else {
                    int thickness = 1;
                    int minY;
                    int width = lastDragPosition.item().rect.getWidth();
                    if (lastDragPosition.position() == ItemPosition.ABOVE) {
                        minY = lastDragPosition.item().rect.getY() - thickness - 1;
                        if (lastDragPosition.above() != null)
                            width = Math.max(width, lastDragPosition.above().rect.getWidth());
                    } else
                        minY = lastDragPosition.item().rect.getBottom() + 1;
                    
                    if (lastDragPosition.above() != null) {
                        dragLine.render(graphics, lastDragPosition.item().rect.getX() - thickness, minY - thickness, thickness, thickness * 3);
                        dragLine.render(graphics, lastDragPosition.item().rect.getX() + width, minY - thickness, thickness, thickness * 3);
                    } else if (lastDragPosition.position() == ItemPosition.ABOVE) {
                        dragLine.render(graphics, lastDragPosition.item().rect.getX() - thickness, minY, thickness, thickness * 2);
                        dragLine.render(graphics, lastDragPosition.item().rect.getX() + width, minY, thickness, thickness * 2);
                    } else {
                        dragLine.render(graphics, lastDragPosition.item().rect.getX() - thickness, minY - thickness, thickness, thickness * 2);
                        dragLine.render(graphics, lastDragPosition.item().rect.getX() + width, minY - thickness, thickness, thickness * 2);
                    }
                    
                    dragLine.render(graphics, lastDragPosition.item().rect.getX(), minY, width, thickness);
                }
            }
            pose.popMatrix();
        } else
            lastDragPosition = null;
        
        super.renderContent(graphics, formatting, borderWidth, controlRect, realRect, scale, mouseX, mouseY);
        
        pose.pushMatrix();
        pose.translate((float) getOffsetX(), (float) (getContentOffset() + getOffsetY()));
        List<GuiTreeLine> lines = new ArrayList<>();
        int size = -1;
        for (GuiControl control : controls) {
            if (!(control instanceof GuiTreeItem item))
                continue;
            int lineY = (control.rect.getY() + control.rect.getBottom()) / 2 + halfLineThickness;
            int level = item.getLevel() - (visibleRoot ? 1 : 2);
            
            if (level <= size) {
                if (level >= 0)
                    lines.get(level).y2 = lineY;
                for (int i = size; i > level; i--) {
                    if (lines.get(i).invalid)
                        continue;
                    
                    lines.get(i).render(graphics);
                    lines.get(i).invalid = false;
                }
                size = level;
            } else {
                if (level < 0)
                    continue;
                
                while (level > size) {
                    if (lines.size() > size + 1)
                        lines.get(level).set(control.rect.getY() - 2, lineY);
                    else
                        lines.add(new GuiTreeLine(size + 1, control.rect.getY() - 2, lineY));
                    size++;
                }
            }
            
            if (level >= 0)
                line.render(graphics, lines.get(level).x + lineThickness, lineY - lineThickness, levelSpacing / 2f, lineThickness);
        }
        
        if (size >= 0) {
            for (int i = size; i > -1; i--) {
                if (lines.get(i).invalid)
                    continue;
                
                lines.get(i).render(graphics);
                lines.get(i).invalid = false;
            }
        }
        
        pose.popMatrix();
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button))
            return true;
        if (canDeselect)
            select(null);
        return true;
    }
    
    private GuiTreeDragPosition createPosition(ItemPosition position, GuiTreeItem item, GuiControl before) {
        if (item == root && position != ItemPosition.IN)
            return null;
        return new GuiTreeDragPosition(position, item, before != null && before instanceof GuiTreeItem item2 && item2.getLevel() == item.getLevel() ? before : null);
    }
    
    protected GuiTreeDragPosition calculatePosition(int mouseX, int mouseY) {
        GuiControl last = null;
        GuiControl before = null;
        for (GuiControl control : controls) {
            if (control == dragged)
                continue;
            if (control instanceof GuiTreeItem item)
                if (control.rect.getY() > mouseY)
                    return createPosition(ItemPosition.ABOVE, item, before);
                else if (control.rect.inside(mouseX, mouseY))
                    return createPosition(ItemPosition.IN, item, before);
                else
                    last = control;
            before = control;
        }
        if (last != null)
            return createPosition(ItemPosition.BELOW, (GuiTreeItem) last, null);
        return null;
    }
    
    public boolean performModication(GuiTreeItem item, GuiTreeDragPosition position) {
        if (item.isChild(position.item()))
            return false;
        
        try {
            item.setMoving(true);
            if (!item.getParentItem().removeItem(item))
                return false;
            position.insert(item);
        } finally {
            item.setMoving(false);
        }
        updateTree();
        return true;
    }
    
    public boolean moveUp() {
        if (selected == null)
            return false;
        GuiTreeItem parent = selected.getParentItem();
        if (parent == null)
            return false;
        int index = parent.indexOf(selected);
        if (index <= 0)
            return false;
        return performModication(selected, new GuiTreeDragPosition(ItemPosition.ABOVE, parent.getItem(index - 1), null));
    }
    
    public boolean moveDown() {
        if (selected == null)
            return false;
        GuiTreeItem parent = selected.getParentItem();
        if (parent == null)
            return false;
        int index = parent.indexOf(selected);
        if (index >= parent.itemsCount() - 1)
            return false;
        return performModication(selected, new GuiTreeDragPosition(ItemPosition.BELOW, parent.getItem(index + 1), null));
    }
    
    public boolean isDragged() {
        return dragged != null;
    }
    
    public void startDrag(GuiTreeItem item) {
        dragged = item;
    }
    
    public boolean endDrag() {
        performModication(dragged, lastDragPosition);
        lastDragPosition = null;
        dragged = null;
        return true;
    }
    
    public static class GuiTreeSelectionChanged extends GuiControlChangedEvent {
        
        public final GuiTreeItem previousSelected;
        public final GuiTreeItem selected;
        
        public GuiTreeSelectionChanged(GuiTree tree, GuiTreeItem previousSelected, GuiTreeItem selected) {
            super(tree);
            this.previousSelected = previousSelected;
            this.selected = selected;
        }
        
    }
    
    private class GuiTreeLine {
        
        public final int x;
        public int y;
        public int y2;
        public boolean invalid;
        
        public GuiTreeLine(int level, int minY, int maxY) {
            this.x = offsetByLevel(level + (visibleRoot ? 1 : 2)) - levelSpacing / 2;
            set(minY, maxY);
        }
        
        public void set(int minY, int maxY) {
            this.y = minY;
            this.y2 = maxY;
            invalid = false;
        }
        
        public void render(GuiGraphics graphics) {
            line.render(graphics, x, y, lineThickness, y2 - y);
        }
    }
    
}
