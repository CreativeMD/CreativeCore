package team.creative.creativecore.server.gui.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.server.gui.GuiServerControl;
import team.creative.creativecore.server.gui.manager.GuiServerManager;

public class GuiServerRegistry {
    
    private static final GuiServerControl EMPTY = new GuiServerControl(null);
    private static final Object2ObjectMap<Class<? extends GuiControl>, Function<GuiControl, GuiServerControl>> CONTROL_FACTORY = new Object2ObjectArrayMap<>();
    private static final List<Function<GuiControl, GuiServerControl>> CONTROL_SPECIAL_FACTORY = new ArrayList<>();
    
    private static final Object2ObjectMap<Class<? extends GuiManager>, Function<GuiManager, GuiServerManager>> MANAGER_FACTORY = new Object2ObjectArrayMap<>();
    
    public static GuiServerControl create(GuiControl control) {
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
    
    public static GuiServerManager create(GuiManager manager) {
        var function = MANAGER_FACTORY.get(manager.getClass());
        if (function != null)
            return function.apply(manager);
        return null;
    }
    
    public static <T extends GuiControl> void register(Class<T> clazz, Function<T, GuiServerControl> factory) {
        CONTROL_FACTORY.put(clazz, (Function<GuiControl, GuiServerControl>) factory);
    }
    
    public static <T extends GuiManager> void registerManager(Class<T> clazz, Function<T, GuiServerManager> factory) {
        MANAGER_FACTORY.put(clazz, (Function<GuiManager, GuiServerManager>) factory);
    }
    
    public static void registerSpecial(Function<GuiControl, GuiServerControl> factory) {
        CONTROL_SPECIAL_FACTORY.add(factory);
    }
    
    static {
        registerSpecial(x -> EMPTY);
    }
    
}
