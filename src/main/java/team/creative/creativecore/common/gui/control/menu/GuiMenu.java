package team.creative.creativecore.common.gui.control.menu;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public abstract class GuiMenu<T> extends GuiScrollY {
    
    public GuiMenu(IGuiParent parent, NamedTree<T> tree) {
        super(parent);
        if (dist() != null)
            dist().init(tree);
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
    
    public class GuiMenuEntry extends GuiLabel {
        
        public NamedTree<T> folder;
        
        public GuiMenuEntry(IGuiParent parent, String name, NamedTree<T> folder) {
            super(parent, name);
            this.folder = folder;
            setTitle(root().translate(name, folder.value != null));
        }
        
        public GuiMenu menu() {
            return GuiMenu.this;
        }
        
    }
    
    public static interface GuiMenuDist<T> extends GuiScrollYDist {
        
        public void closed();
        
        public void init(NamedTree<T> tree);
        
        public boolean isRoot();
        
        public GuiMenuRoot<T> root();
        
        public void buildTree();
        
    }
}
