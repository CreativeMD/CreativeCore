package team.creative.creativecore.common.gui.control.tree;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.parent.GuiScrollXY;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.control.tree.GuiTreeDragPosition.ItemPosition;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.type.itr.NestedFunctionIterator;
import team.creative.creativecore.common.util.type.itr.TreeIterator;

public class GuiTree extends GuiScrollXY {
    
    private boolean searchbar = false;
    private boolean checkboxes = false;
    private boolean checkboxesPartial = false;
    protected boolean canDeselect = true;
    private final GuiTextfield search;
    private boolean visibleRoot = false;
    private final GuiTreeItem root;
    
    private GuiTreeItem selected = null;
    
    public GuiTree(IGuiParent parent, String name) {
        this(parent, name, false);
    }
    
    public GuiTree(IGuiParent parent, String name, boolean searchbar) {
        super(parent, name, GuiFlow.STACK_Y);
        this.searchbar = searchbar;
        if (searchbar)
            search = new GuiTextfield(this, name);
        else
            search = null;
        this.root = new GuiTreeItem(this, "root", this);
        setSpacing(3);
        setLineThickness(1);
    }
    
    @Override
    public GuiTreeDist dist() {
        return (GuiTreeDist) super.dist();
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
    
    public boolean canDeselect() {
        return canDeselect;
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
    
    public GuiTree setLineThickness(int thickness) {
        dist().setLineThickness(thickness);
        return this;
    }
    
    public GuiTree setRootVisibility(boolean visible) {
        visibleRoot = visible;
        return this;
    }
    
    public boolean isRootVisible() {
        return visibleRoot;
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
        
        dist().reflowTree();
    }
    
    private void addItem(GuiTreeItem item) {
        for (GuiTreeItem subItem : item.items()) {
            add(subItem);
            if (subItem.opened())
                addItem(subItem);
        }
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
        return dist().isDragged();
    }
    
    public void startDrag(GuiTreeItem item) {
        dist().startDrag(item);
    }
    
    public boolean endDrag() {
        return dist().endDrag();
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
    
    public static interface GuiTreeDist extends GuiScrollXYDist {
        
        public void reflowTree();
        
        public void setLineThickness(int thickness);
        
        public boolean isDragged();
        
        public void startDrag(GuiTreeItem item);
        
        public boolean endDrag();
        
    }
    
}
