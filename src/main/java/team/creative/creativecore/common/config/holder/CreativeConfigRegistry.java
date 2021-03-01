package team.creative.creativecore.common.config.holder;

import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class CreativeConfigRegistry extends ConfigHolderDynamic {
    
    public CreativeConfigRegistry() {
        super();
    }
    
    public static final CreativeConfigRegistry ROOT = new CreativeConfigRegistry();
    
    @Override
    public ConfigHolderDynamic registerValue(String key, Object defaultValue) {
        return registerValue(key, defaultValue, ConfigSynchronization.UNIVERSAL, false);
    }
    
    @Override
    public ConfigHolderDynamic registerValue(String key, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
        return super.registerValue(key, defaultValue, synchronization, requiresRestart);
    }
    
    public ICreativeConfigHolder followPath(String... path) {
        ICreativeConfigHolder current = this;
        for (int i = 0; i < path.length; i++) {
            Object object = current.get(path[i]);
            if (object instanceof ICreativeConfigHolder)
                current = (ICreativeConfigHolder) object;
            else
                return null;
        }
        
        return current;
    }
    
    public ConfigKey findKey(String[] path) {
        ICreativeConfigHolder current = this;
        for (int i = 0; i < path.length; i++) {
            if (i + 1 == path.length)
                return current.getField(path[i]);
            Object object = current.get(path[i]);
            if (object instanceof ICreativeConfigHolder)
                current = (ICreativeConfigHolder) object;
            else
                return null;
        }
        return null;
    }
    
    public boolean removeField(String modid) {
        return fields.removeKey(modid);
    }
    
    public static void load(String modid, Dist side) {
        CreativeCore.CONFIG_HANDLER.load(modid, side);
    }
    
}
