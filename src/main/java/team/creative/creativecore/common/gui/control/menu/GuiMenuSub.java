package team.creative.creativecore.common.gui.control.menu;

import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public class GuiMenuSub<T> extends GuiMenu<T> {
    
    protected final GuiMenuRoot<T> root;
    protected final GuiExtensionCreator<GuiMenu, GuiMenu> parent;
    
    public GuiMenuSub(GuiMenuRoot<T> root, NamedTree<T> tree, GuiExtensionCreator<GuiMenu, GuiMenu> parent) {
        super(tree);
        this.root = root;
        this.parent = parent;
        buildTree();
    }
    
    @Override
    public boolean isRoot() {
        return false;
    }
    
    @Override
    public GuiMenuRoot<T> root() {
        return root;
    }
    
    @Override
    public GuiExtensionCreator<? extends GuiControl, GuiMenu> parentCreator() {
        return parent;
    }
    
}
