package team.creative.creativecore.common.gui.control.menu;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public class GuiMenuRoot<T> extends GuiMenu<T> {
    
    protected final GuiExtensionCreator<? extends GuiControl, ? extends GuiMenu> parent;
    protected final Function<String, Component> folderTitle;
    protected final Function<T, Component> valueTitle;
    protected final Consumer<T> clicked;
    
    public GuiMenuRoot(NamedTree<T> tree, GuiExtensionCreator<? extends GuiControl, ? extends GuiMenu> parent, Function<String, Component> folderTitle, Function<T, Component> valueTitle, Consumer<T> clicked) {
        super(tree);
        this.parent = parent;
        this.folderTitle = folderTitle;
        this.valueTitle = valueTitle;
        this.clicked = clicked;
        buildTree();
    }
    
    @Override
    public boolean isRoot() {
        return true;
    }
    
    @Override
    public GuiMenuRoot<T> root() {
        return this;
    }
    
    @Override
    public GuiExtensionCreator<? extends GuiControl, ? extends GuiMenu> parentCreator() {
        return parent;
    }
    
    public Component translateFolder(String path) {
        return ((MutableComponent) folderTitle.apply(path)).append(" >");
    }
    
    public Component translateValue(T value) {
        return valueTitle.apply(value);
    }
    
    public void select(T value) {
        if (submenu.hasExtension())
            submenu.close();
        parent.close();
        clicked.accept(value);
    }
    
}
