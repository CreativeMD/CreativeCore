package team.creative.creativecore.client.gui.control.menu;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.control.menu.GuiMenu;
import team.creative.creativecore.common.gui.control.menu.GuiMenuRoot;
import team.creative.creativecore.common.gui.control.menu.GuiMenuRoot.GuiMenuRootDist;

public class GuiClientMenuRoot<K, T extends GuiMenuRoot<K>> extends GuiClientMenu<K, T> implements GuiMenuRootDist<K> {
    
    public GuiExtensionCreator<? extends GuiClientControl, ? extends GuiMenu> parent;
    protected Function<String, Component> title;
    protected BiConsumer<String, K> clicked;
    
    public GuiClientMenuRoot(T control) {
        super(control);
    }
    
    @Override
    public void set(Function<String, Component> title, BiConsumer<String, K> clicked) {
        this.title = title;
        this.clicked = clicked;
    }
    
    @Override
    public boolean isRoot() {
        return true;
    }
    
    @Override
    public GuiMenuRoot<K> root() {
        return control;
    }
    
    @Override
    public GuiExtensionCreator<? extends GuiClientControl, ? extends GuiMenu> parentCreator() {
        return parent;
    }
    
    @Override
    public Component translate(String path, boolean hasValue) {
        Component c = title.apply(path);
        if (!hasValue)
            c = ((MutableComponent) c).append(" >");
        return c;
    }
    
    @Override
    public void select(String path, K value) {
        if (submenu.hasExtension())
            submenu.close();
        parent.close();
        clicked.accept(path, value);
    }
}
