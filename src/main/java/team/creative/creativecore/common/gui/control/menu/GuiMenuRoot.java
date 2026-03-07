package team.creative.creativecore.common.gui.control.menu;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public class GuiMenuRoot<T> extends GuiMenu<T> {
    
    protected final GuiExtensionCreator<? extends GuiControl, ? extends GuiMenu> parent;
    protected final Function<String, Component> title;
    protected final BiConsumer<String, T> clicked;
    
    public GuiMenuRoot(NamedTree<T> tree, GuiExtensionCreator<? extends GuiControl, ? extends GuiMenu> parent, Function<String, Component> title, BiConsumer<String, T> clicked) {
        super(tree);
        this.parent = parent;
        this.title = title;
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
    
    public Component translate(String path, boolean hasValue) {
        Component c = title.apply(path);
        if (!hasValue)
            c = ((MutableComponent) c).append(" >");
        return c;
    }
    
    public void select(String path, T value) {
        if (submenu.hasExtension())
            submenu.close();
        parent.close();
        clicked.accept(path, value);
    }
    
    public NamedTree<GuiMenuEntry> createEntryTree() {
        NamedTree<GuiMenuEntry> entryTree = new NamedTree<>();
        populateEntryTree(entryTree);
        return entryTree;
    }
    
}
