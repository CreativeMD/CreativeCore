package team.creative.creativecore.common.gui.control.tree;

import org.jetbrains.annotations.Nullable;

import team.creative.creativecore.common.gui.GuiControl;

public record GuiTreeDragPosition(team.creative.creativecore.common.gui.control.tree.GuiTreeDragPosition.ItemPosition position, GuiTreeItem item, GuiControl above) {
    
    public GuiTreeDragPosition(ItemPosition position, GuiTreeItem item, @Nullable GuiControl above) {
        this.position = position;
        this.item = item;
        this.above = above;
    }
    
    public void insert(GuiTreeItem item) {
        position.insert(item, this.item);
    }
    
    @Override
    public String toString() {
        return position.name() + "," + item;
    }
    
    public enum ItemPosition {
        
        ABOVE {
            @Override
            public void insert(GuiTreeItem toAdd, GuiTreeItem reference) {
                GuiTreeItem parent = reference.getParentItem();
                parent.insertItem(parent.indexOf(reference), toAdd);
            }
        },
        IN {
            @Override
            public void insert(GuiTreeItem toAdd, GuiTreeItem reference) {
                reference.addItem(toAdd);
            }
        },
        BELOW {
            @Override
            public void insert(GuiTreeItem toAdd, GuiTreeItem reference) {
                GuiTreeItem parent = reference.getParentItem();
                int index = parent.indexOf(reference);
                if (index >= parent.itemsCount() - 1)
                    parent.addItem(toAdd);
                else
                    parent.insertItem(index + 1, toAdd);
            }
        };
        
        public abstract void insert(GuiTreeItem toAdd, GuiTreeItem reference);
        
    }
    
}
