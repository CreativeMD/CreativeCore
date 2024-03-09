package team.creative.creativecore.common.gui.controls.tree;

import org.jetbrains.annotations.Nullable;
import team.creative.creativecore.common.gui.GuiChildControl;

public record GuiTreeDragPosition(
        team.creative.creativecore.common.gui.controls.tree.GuiTreeDragPosition.ItemPosition position,
        GuiChildControl child, GuiTreeItem item, GuiChildControl above) {

    public GuiTreeDragPosition(ItemPosition position, GuiChildControl child, GuiTreeItem item, @Nullable GuiChildControl above) {
        this.position = position;
        this.child = child;
        this.item = item;
        this.above = above;
    }

    public void insert(GuiTreeItem item) {
        position.insert(item, this.item);
    }

    @Override
    public String toString() {
        return position.name() + "," + child;
    }

    public static enum ItemPosition {

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
