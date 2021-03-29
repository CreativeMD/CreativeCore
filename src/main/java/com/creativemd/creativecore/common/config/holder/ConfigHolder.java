package com.creativemd.creativecore.common.config.holder;

import java.util.Collection;

import com.creativemd.creativecore.common.config.converation.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.relauncher.Side;

public abstract class ConfigHolder<T extends ConfigKey> implements ICreativeConfigHolder {
    
    public final ConfigSynchronization synchronization;
    public final ICreativeConfigHolder parent;
    public final String[] path;
    
    protected PairList<String, T> fields = new PairList<>();
    
    public ConfigHolder(ICreativeConfigHolder parent, String key, ConfigSynchronization synchronization) {
        this.parent = parent;
        this.path = ObjectArrays.concat(parent.path(), key);
        this.synchronization = synchronization;
    }
    
    ConfigHolder() {
        this.parent = null;
        this.path = new String[] {};
        this.synchronization = ConfigSynchronization.UNIVERSAL;
    }
    
    @Override
    public ConfigSynchronization synchronization() {
        return synchronization;
    }
    
    @Override
    public ICreativeConfigHolder parent() {
        return parent;
    }
    
    @Override
    public String[] path() {
        return path;
    }
    
    @Override
    public ConfigKey getField(String key) {
        return fields.getValue(key);
    }
    
    @Override
    public Collection<? extends ConfigKey> fields() {
        return fields.values();
    }
    
    @Override
    public Collection<String> names() {
        return fields.keys();
    }
    
    @Override
    public Object get(String key) {
        T field = fields.getValue(key);
        if (field != null)
            return field.get();
        return null;
    }
    
    @Override
    public boolean isEmpty(Side side) {
        for (int i = 0; i < fields.size(); i++) {
            T field = fields.get(i).value;
            if (field.get() instanceof ICreativeConfigHolder) {
                if (!((ICreativeConfigHolder) field.get()).isEmpty(side))
                    return false;
            } else if (field.is(side))
                return false;
        }
        return true;
    }
    
    @Override
    public boolean isEmptyWithoutForce(Side side) {
        for (int i = 0; i < fields.size(); i++) {
            T field = fields.get(i).value;
            if (field.get() instanceof ICreativeConfigHolder) {
                if (!((ICreativeConfigHolder) field.get()).isEmptyWithoutForce(side))
                    return false;
            } else if (field.isWithoutForce(side))
                return false;
        }
        return true;
    }
    
    @Override
    public boolean isDefault(Side side) {
        for (int i = 0; i < fields.size(); i++)
            if (fields.get(i).value.is(side) && !fields.get(i).value.isDefault(side))
                return false;
        return true;
    }
    
    @Override
    public void restoreDefault(Side side, boolean ignoreRestart) {
        for (int i = 0; i < fields.size(); i++) {
            T key = fields.get(i).value;
            if (key.is(side) && (!ignoreRestart || !key.requiresRestart) && (!(key.get() instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder) key.get()).isEmpty(side)))
                key.restoreDefault(side, ignoreRestart);
        }
    }
    
    @Override
    public void load(boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side) {
        for (int i = 0; i < fields.size(); i++) {
            T field = fields.get(i).value;
            if (field.is(side) && (!ignoreRestart || !field.requiresRestart))
                if (json.has(field.name))
                    field.set(ConfigTypeConveration
                        .read(field.getType(), field.getDefault(), loadDefault, ignoreRestart, json.get(field.name), side, field instanceof ConfigKeyField ? (ConfigKeyField) field : null));
                else if (loadDefault && (!(field.get() instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder) field.get()).isEmpty(side)))
                    field.restoreDefault(side, ignoreRestart);
                
        }
    }
    
    @Override
    public JsonObject save(boolean saveDefault, boolean ignoreRestart, Side side) {
        JsonObject object = new JsonObject();
        for (int i = 0; i < fields.size(); i++) {
            T field = fields.get(i).value;
            if (field.is(side) && (!ignoreRestart || !field.requiresRestart) && (saveDefault || !field.isDefault(side)))
                object.add(field.name, ConfigTypeConveration
                    .write(field.getType(), field.get(), field.getDefault(), saveDefault, ignoreRestart, side, field instanceof ConfigKeyField ? (ConfigKeyField) field : null));
        }
        return object;
    }
    
}
