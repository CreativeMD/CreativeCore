package team.creative.creativecore.client.gui.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.GuiClientLayer;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.client.gui.manager.GuiClientManager;
import team.creative.creativecore.client.gui.manager.GuiClientManagerItem;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;

public class GuiClientRegistry {
    
    private static final Object2ObjectMap<Class<? extends GuiControl>, Function<GuiControl, GuiClientControl>> CONTROL_FACTORY = new Object2ObjectArrayMap<>();
    private static final List<Function<GuiControl, GuiClientControl>> CONTROL_SPECIAL_FACTORY = new ArrayList<>();
    
    private static final Object2ObjectMap<Class<? extends GuiManager>, Function<GuiManager, GuiClientManager>> MANAGER_FACTORY = new Object2ObjectArrayMap<>();
    
    public static GuiClientControl create(GuiControl control) {
        var function = CONTROL_FACTORY.get(control.getClass());
        if (function != null)
            return function.apply(control);
        for (int i = 0; i < CONTROL_SPECIAL_FACTORY.size(); i++) {
            var result = CONTROL_SPECIAL_FACTORY.get(i).apply(control);
            if (result != null)
                return result;
        }
        return null;
    }
    
    public static GuiClientManager create(GuiManager manager) {
        var function = MANAGER_FACTORY.get(manager.getClass());
        if (function != null)
            return function.apply(manager);
        return null;
    }
    
    public static <T extends GuiControl> void register(Class<T> clazz, Function<T, GuiClientControl> factory) {
        CONTROL_FACTORY.put(clazz, (Function<GuiControl, GuiClientControl>) factory);
    }
    
    public static <T extends GuiManager> void registerManager(Class<T> clazz, Function<T, GuiClientManager> factory) {
        MANAGER_FACTORY.put(clazz, (Function<GuiManager, GuiClientManager>) factory);
    }
    
    public static void registerSpecial(Function<GuiControl, GuiClientControl> factory) {
        CONTROL_SPECIAL_FACTORY.add(factory);
    }
    
    static {
        register(GuiParent.class, GuiClientParent::new);
        register(GuiLayer.class, GuiClientLayer::new);
        
        registerManager(GuiManagerItem.class, GuiClientManagerItem::new);
    }
    
}
