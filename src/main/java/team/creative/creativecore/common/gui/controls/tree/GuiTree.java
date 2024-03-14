package team.creative.creativecore.common.gui.controls.tree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollXY;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.gui.controls.tree.GuiTreeDragPosition.ItemPosition;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.type.itr.NestedFunctionIterator;
import team.creative.creativecore.common.util.type.itr.TreeIterator;

import java.util.ArrayList;
import java.util.List;

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
        for (GuiChildControl child : controls)
            if (child.control instanceof GuiTreeItem item)
                width = Math.max(width, offsetByLevel(item.getLevel()) + 1 + child.getPreferredWidth(availableWidth));
            else
                width = Math.max(width, child.getPreferredWidth(availableWidth));
        return width;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        for (GuiChildControl child : controls)
            if (child.control instanceof GuiTreeItem item)
                child.setX(offsetByLevel(item.getLevel()) + 1);
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
    protected void renderContent(PoseStack pose, GuiChildControl control, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        if (isDragged()) {
            pose.pushPose();
            pose.translate(getContentOffset() + getOffsetX(), getContentOffset() + getOffsetY(), 0);
            lastDragPosition = calculatePosition((int) (mouseX - realRect.minX - getContentOffset()), (int) (mouseY - realRect.minY - getContentOffset()));
            if (lastDragPosition != null) {
                if (lastDragPosition.position() == ItemPosition.IN)
                    dragHover.render(pose, lastDragPosition.child().rect.minX, lastDragPosition.child().rect.minY, lastDragPosition.child().rect.getWidth(), lastDragPosition.child().rect
                            .getHeight());
                else {
                    int thickness = 1;
                    int minY;
                    int width = (int) lastDragPosition.child().rect.getWidth();
                    if (lastDragPosition.position() == ItemPosition.ABOVE) {
                        minY = (int) (lastDragPosition.child().rect.minY - thickness) - 1;
                        if (lastDragPosition.above() != null)
                            width = Math.max(width, (int) lastDragPosition.above().rect.getWidth());
                    } else
                        minY = (int) lastDragPosition.child().rect.maxY + 1;
                    
                    if (lastDragPosition.above() != null) {
                        dragLine.render(pose, lastDragPosition.child().rect.minX - thickness, minY - thickness, thickness, thickness * 3);
                        dragLine.render(pose, lastDragPosition.child().rect.minX + width, minY - thickness, thickness, thickness * 3);
                    } else if (lastDragPosition.position() == ItemPosition.ABOVE) {
                        dragLine.render(pose, lastDragPosition.child().rect.minX - thickness, minY, thickness, thickness * 2);
                        dragLine.render(pose, lastDragPosition.child().rect.minX + width, minY, thickness, thickness * 2);
                    } else {
                        dragLine.render(pose, lastDragPosition.child().rect.minX - thickness, minY - thickness, thickness, thickness * 2);
                        dragLine.render(pose, lastDragPosition.child().rect.minX + width, minY - thickness, thickness, thickness * 2);
                    }
                    
                    dragLine.render(pose, lastDragPosition.child().rect.minX, minY, width, thickness);
                }
            }
            pose.popPose();
        } else
            lastDragPosition = null;
        
        super.renderContent(pose, control, formatting, borderWidth, controlRect, realRect, scale, mouseX, mouseY);
        
        pose.pushPose();
        pose.translate(getOffsetX(), getContentOffset() + getOffsetY(), 0);
        List<GuiTreeLine> lines = new ArrayList<>();
        int size = -1;
        for (GuiChildControl child : controls) {
            if (!(child.control instanceof GuiTreeItem item))
                continue;
            int lineY = (int) ((child.rect.minY + child.rect.maxY) / 2) + halfLineThickness;
            int level = item.getLevel() - (visibleRoot ? 1 : 2);
            
            if (level <= size) {
                if (level >= 0)
                    lines.get(level).y2 = lineY;
                for (int i = size; i > level; i--) {
                    if (lines.get(i).invalid)
                        continue;
                    
                    lines.get(i).render(pose);
                    lines.get(i).invalid = false;
                }
                size = level;
            } else {
                if (level < 0)
                    continue;
                
                while (level > size) {
                    if (lines.size() > size + 1)
                        lines.get(level).set((int) child.rect.minY - 2, lineY);
                    else
                        lines.add(new GuiTreeLine(size + 1, (int) child.rect.minY - 2, lineY));
                    size++;
                }
            }
            
            if (level >= 0)
                line.render(pose, lines.get(level).x + lineThickness, lineY - lineThickness, levelSpacing / 2f, lineThickness);
        }
        
        if (size >= 0) {
            for (int i = size; i > -1; i--) {
                if (lines.get(i).invalid)
                    continue;
                
                lines.get(i).render(pose);
                lines.get(i).invalid = false;
            }
        }
        
        pose.popPose();
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (super.mouseClicked(rect, x, y, button))
            return true;
        if (canDeselect)
            select(null);
        return true;
    }
    
    private GuiTreeDragPosition createPosition(ItemPosition position, GuiChildControl child, GuiTreeItem item, GuiChildControl before) {
        if (item == root && position != ItemPosition.IN)
            return null;
        return new GuiTreeDragPosition(position, child, item, before != null && before.control instanceof GuiTreeItem item2 && item2.getLevel() == item.getLevel() ? before : null);
    }
    
    protected GuiTreeDragPosition calculatePosition(int mouseX, int mouseY) {
        GuiChildControl last = null;
        GuiChildControl before = null;
        for (GuiChildControl child : controls) {
            if (child.control == dragged)
                continue;
            if (child.control instanceof GuiTreeItem item)
                if (child.rect.minY > mouseY)
                    return createPosition(ItemPosition.ABOVE, child, item, before);
                else if (child.rect.inside(mouseX, mouseY))
                    return createPosition(ItemPosition.IN, child, item, before);
                else
                    last = child;
            before = child;
        }
        if (last != null)
            return createPosition(ItemPosition.BELOW, last, (GuiTreeItem) last.control, null);
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
        return performModication(selected, new GuiTreeDragPosition(ItemPosition.ABOVE, null, parent.getItem(index - 1), null));
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
        return performModication(selected, new GuiTreeDragPosition(ItemPosition.BELOW, null, parent.getItem(index + 1), null));
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
        
        public void render(PoseStack pose) {
            line.render(pose, x, y, lineThickness, y2 - y);
        }
    }
    
}
