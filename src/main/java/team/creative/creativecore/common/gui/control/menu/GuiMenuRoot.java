package team.creative.creativecore.common.gui.control.menu;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public class GuiMenuRoot<T> extends GuiMenu<T> {
    
    public GuiMenuRoot(IGuiParent parent, NamedTree<T> tree, Function<String, Component> title, BiConsumer<String, T> clicked) {
        super(parent, tree);
        if (dist() != null) {
            dist().set(title, clicked);
            dist().buildTree();
        }
    }
    
    @Override
    public GuiMenuRootDist<T> dist() {
        return (GuiMenuRootDist<T>) super.dist();
    }
    
    public Component translate(String path, boolean hasValue) {
        if (dist() != null)
            return dist().translate(path, hasValue);
        return Component.empty();
    }
    
    public void select(String path, T value) {
        if (dist() != null)
            dist().select(path, value);
    }
    
    public static interface GuiMenuRootDist<T> extends GuiMenuDist<T> {
        
        public void set(Function<String, Component> title, BiConsumer<String, T> clicked);
        
        public Component translate(String path, boolean hasValue);
        
        public void select(String path, T value);
        
    }
    
}
