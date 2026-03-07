package team.creative.creativecore.common.gui.control.menu;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiLabel.GuiLabelDist;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public abstract class GuiMenu<T> extends GuiScrollY {
    
    public GuiMenu(IGuiParent parent, NamedTree<T> tree) {
        super(parent);
        if (dist() != null)
            dist().init(tree);
    }
    
    public void populateEntryTree(NamedTree<GuiMenuEntry> entryTree) {
        if (dist() != null)
            dist().populateEntryTree(entryTree);
    }
    
    @Override
    public GuiMenuDist<T> dist() {
        return (GuiMenuDist<T>) super.dist();
    }
    
    @Override
    public void closed() {
        if (dist() != null)
            dist().closed();
    }
    
    public boolean isRoot() {
        if (dist() != null)
            return dist().isRoot();
        return false;
    }
    
    public GuiMenuRoot<T> root() {
        if (dist() != null)
            return dist().root();
        return null;
    }
    
    public static class GuiMenuEntry<T> extends GuiLabel {
        
        public NamedTree<T> folder;
        
        public GuiMenuEntry(GuiMenu<T> menu, String name, String title, NamedTree<T> folder) {
            super(menu, name);
            this.folder = folder;
            setTitle(menu.root().translate(title, folder.value != null));
        }
        
        @Override
        public GuiMenuEntryDist dist() {
            return (GuiMenuEntryDist) super.dist();
        }
        
        public void setHighlighted(boolean value) {
            if (dist() != null)
                dist().setHighlighted(value);
        }
        
        public void close() {
            if (dist() != null)
                dist().close();
        }
        
        public void open() {
            if (dist() != null)
                dist().open();
        }
        
        public GuiMenu<T> menu() {
            return (GuiMenu<T>) getParent();
        }
        
    }
    
    public static interface GuiMenuDist<T> extends GuiScrollYDist {
        
        public void closed();
        
        public void init(NamedTree<T> tree);
        
        public boolean isRoot();
        
        public GuiMenuRoot<T> root();
        
        public void buildTree();
        
        public void populateEntryTree(NamedTree<GuiMenuEntry> entryTree);
        
    }
    
    public static interface GuiMenuEntryDist extends GuiLabelDist {
        
        public void setHighlighted(boolean value);
        
        public void close();
        
        public void open();
    }
}
