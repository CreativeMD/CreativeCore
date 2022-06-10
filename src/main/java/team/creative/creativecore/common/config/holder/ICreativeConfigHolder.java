package team.creative.creativecore.common.config.holder;

import java.util.Collection;

import com.google.gson.JsonObject;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.IConfigObject;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public interface ICreativeConfigHolder extends IConfigObject {
    
    public ICreativeConfigHolder parent();
    
    public default String getName() {
        return path()[path().length - 1];
    }
    
    public String[] path();
    
    public Collection<? extends ConfigKey> fields();
    
    public Collection<String> names();
    
    public Object get(String key);
    
    public ConfigKey getField(String key);
    
    public void load(boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side);
    
    public JsonObject save(boolean saveDefault, boolean ignoreRestart, Side side);
    
    public boolean isEmpty(Side side);
    
    public boolean isEmptyWithoutForce(Side side);
    
    public ConfigSynchronization synchronization();
    
}
