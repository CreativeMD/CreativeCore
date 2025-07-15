package team.creative.creativecore.server.gui.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.server.gui.GuiServerControl;

public class GuiServerControlRegistry {
    
    private static final Object2ObjectMap<Class<? extends GuiControl>, Function<GuiControl, GuiServerControl>> FACTORY = new Object2ObjectArrayMap<>();
    private static final List<Function<GuiControl, GuiServerControl>> SPECIAL_FACTORY = new ArrayList<>();
    
    public static GuiServerControl create(GuiControl control) {
        var function = FACTORY.get(control.getClass());
        if (function != null)
            return function.apply(control);
        for (int i = 0; i < SPECIAL_FACTORY.size(); i++) {
            var result = SPECIAL_FACTORY.get(i).apply(control);
            if (result != null)
                return result;
        }
        return null;
    }
    
    public static <T extends GuiControl> void register(Class<T> clazz, Function<T, GuiServerControl> factory) {
        FACTORY.put(clazz, (Function<GuiControl, GuiServerControl>) factory);
    }
    
    public static void registerSpecial(Function<GuiControl, GuiServerControl> factory) {
        SPECIAL_FACTORY.add(factory);
    }
    
    static {
        
    }
    
}
