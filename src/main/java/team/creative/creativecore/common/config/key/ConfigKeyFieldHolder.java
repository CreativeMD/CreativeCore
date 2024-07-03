package team.creative.creativecore.common.config.key;

import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup.Provider;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.field.ConfigField;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class ConfigKeyFieldHolder extends ConfigKeyField {
    
    public final ICreativeConfigHolder holder;
    
    public ConfigKeyFieldHolder(ICreativeConfigHolder holder, ConfigField field, String name, ConfigSynchronization synchronization, boolean requiresRestart) {
        super(field, name, synchronization, requiresRestart);
        this.holder = holder;
    }
    
    @Override
    public void triggerConfigured(Side side) {
        holder.configured(side);
    }
    
    @Override
    public boolean isDefault(Side side) {
        return holder.isDefault(side);
    }
    
    @Override
    public boolean isDefault(Object value, Side side) {
        return holder.isDefault(side);
    }
    
    @Override
    public void restoreDefault(Side side, boolean ignoreRestart) {
        holder.restoreDefault(side, ignoreRestart);
    }
    
    @Override
    public boolean isFolder() {
        return true;
    }
    
    @Override
    public ICreativeConfigHolder holder() {
        return holder;
    }
    
    @Override
    public Object copy(Provider provider, Side side) {
        Object value = ConfigTypeConveration.createObject(this);
        JsonElement element = write(provider, false, true, side);
        ICreativeConfigHolder.read(provider, ConfigHolderObject.createUnrelated(side, value, get()), true, true, element, side);
        return value;
    }
    
    @Override
    public void read(Provider provider, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side) {
        ICreativeConfigHolder.read(provider, holder, loadDefault, ignoreRestart, element, side);
    }
    
    @Override
    public JsonElement write(Provider provider, boolean saveDefault, boolean ignoreRestart, Side side) {
        return ICreativeConfigHolder.write(provider, holder, saveDefault, ignoreRestart, side);
    }
    
}
