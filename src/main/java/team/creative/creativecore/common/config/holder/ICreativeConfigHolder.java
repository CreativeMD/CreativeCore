package team.creative.creativecore.common.config.holder;

import java.util.Collection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.IConfigObject;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public interface ICreativeConfigHolder extends IConfigObject {
    
    public static void read(HolderLookup.Provider provider, ICreativeConfigHolder holder, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side) {
        if (element.isJsonObject())
            holder.load(provider, loadDefault, ignoreRestart, (JsonObject) element, side);
        else
            holder.restoreDefault(side, ignoreRestart);
    }
    
    public static JsonElement write(HolderLookup.Provider provider, ICreativeConfigHolder holder, boolean saveDefault, boolean ignoreRestart, Side side) {
        return holder.save(provider, saveDefault, ignoreRestart, side);
    }
    
    public ICreativeConfigHolder parent();
    
    public default String getName() {
        return path()[path().length - 1];
    }
    
    public String[] path();
    
    public Collection<? extends ConfigKey> fields();
    
    public Collection<String> names();
    
    public Object get(String key);
    
    public ConfigKey getField(String key);
    
    public void load(HolderLookup.Provider provider, boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side);
    
    public JsonObject save(HolderLookup.Provider provider, boolean saveDefault, boolean ignoreRestart, Side side);
    
    public boolean isEmpty(Side side);
    
    public boolean isEmptyWithoutForce(Side side);
    
    public ConfigSynchronization synchronization();
    
}
