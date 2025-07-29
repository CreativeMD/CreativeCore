package team.creative.creativecore.client.gui.control.menu;

import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.control.menu.GuiMenu;
import team.creative.creativecore.common.gui.control.menu.GuiMenuRoot;
import team.creative.creativecore.common.gui.control.menu.GuiMenuSub;
import team.creative.creativecore.common.gui.control.menu.GuiMenuSub.GuiMenuSubDist;

public class GuiClientMenuSub<K, T extends GuiMenuSub<K>> extends GuiClientMenu<K, T> implements GuiMenuSubDist<K> {
    
    protected GuiMenuRoot<K> root;
    public GuiExtensionCreator<GuiClientMenu, GuiMenu> parent;
    
    public GuiClientMenuSub(T control) {
        super(control);
    }
    
    @Override
    public boolean isRoot() {
        return false;
    }
    
    @Override
    public void setRoot(GuiMenuRoot<K> root) {
        this.root = root;
    }
    
    @Override
    public GuiMenuRoot<K> root() {
        return root;
    }
    
    @Override
    public GuiExtensionCreator<? extends GuiClientControl, GuiMenu> parentCreator() {
        return parent;
    }
    
}
