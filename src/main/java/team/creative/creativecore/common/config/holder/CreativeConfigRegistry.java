package team.creative.creativecore.common.config.holder;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.key.ConfigKeyField;
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
            var field = current.getField(path[i]);
            if (field.isFolder())
                current = field.holder();
            else
                return null;
        }
        
        return current;
    }
    
    public ConfigKeyField findKey(String[] path) {
        ICreativeConfigHolder current = this;
        for (int i = 0; i < path.length; i++) {
            if (i + 1 == path.length)
                return current.getField(path[i]);
            var field = current.getField(path[i]);
            if (field.isFolder())
                current = field.holder();
            else
                return null;
        }
        return null;
    }
    
    public boolean removeField(String modid) {
        return fields.removeKey(modid);
    }
    
    public static void load(HolderLookup.Provider provider, String modid, Side side) {
        CreativeCore.CONFIG_HANDLER.load(provider, modid, side);
    }
    
}
