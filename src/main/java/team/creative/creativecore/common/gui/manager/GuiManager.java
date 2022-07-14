package team.creative.creativecore.common.gui.manager;

import java.util.function.Function;

import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

public abstract class GuiManager {
    
    public static final NamedHandlerRegistry<GuiManagerType> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static <T extends GuiManager> GuiManagerType<T> register(String name, Class<T> managerClass, Function<GuiLayer, T> factory) {
        GuiManagerType<T> type = new GuiManagerType<T>(name, managerClass, factory);
        REGISTRY.register(name, type);
        return type;
    }
    
    public static final GuiManagerType<GuiManagerItem> ITEM = register("item", GuiManagerItem.class, GuiManagerItem::new);
    
    public static record GuiManagerType<T extends GuiManager> (String name, Class<T> managerClass, Function<GuiLayer, T> factory) {}
    
    public final GuiLayer layer;
    
    public GuiManager(GuiLayer layer) {
        this.layer = layer;
    }
    
}
