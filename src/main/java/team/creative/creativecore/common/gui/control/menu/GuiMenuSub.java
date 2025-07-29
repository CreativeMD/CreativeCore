package team.creative.creativecore.common.gui.control.menu;

import team.creative.creativecore.common.util.type.tree.NamedTree;

public class GuiMenuSub<T> extends GuiMenu<T> {
    
    public GuiMenuSub(GuiMenuRoot<T> root, NamedTree<T> tree) {
        super(root.getParent(), tree);
        dist().setRoot(root);
        dist().buildTree();
    }
    
    @Override
    public GuiMenuSubDist<T> dist() {
        return (GuiMenuSubDist<T>) super.dist();
    }
    
    public static interface GuiMenuSubDist<T> extends GuiMenuDist<T> {
        
        public void setRoot(GuiMenuRoot<T> root);
        
    }
    
}
