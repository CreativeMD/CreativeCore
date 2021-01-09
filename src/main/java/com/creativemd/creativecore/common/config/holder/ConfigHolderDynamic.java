package com.creativemd.creativecore.common.config.holder;

import java.lang.reflect.Field;

import com.creativemd.creativecore.common.config.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyDynamic;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyDynamicField;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;

public class ConfigHolderDynamic extends ConfigHolder<ConfigKey> {
    
    public ConfigHolderDynamic(ICreativeConfigHolder parent, String key, ConfigSynchronization synchronization) {
        super(parent, key, synchronization);
    }
    
    ConfigHolderDynamic() {
        super();
    }
    
    public ConfigHolderDynamic registerFolder(String key) {
        return registerFolder(key, ConfigSynchronization.UNIVERSAL);
    }
    
    public ConfigHolderDynamic registerFolder(String key, ConfigSynchronization synchronization) {
        if (key.contains(".") || key.contains("/"))
            throw new RuntimeException("Invalid key " + key + "");
        if (fields.containsKey(key))
            throw new RuntimeException("Key already registered " + key + "");
        
        synchronization = this.synchronization != ConfigSynchronization.UNIVERSAL ? this.synchronization : synchronization;
        
        ConfigHolderDynamic holder = new ConfigHolderDynamic(this, key, synchronization);
        fields.add(key, new ConfigKeyDynamic(key, holder, synchronization, false));
        return holder;
    }
    
    public ConfigHolderDynamic registerValue(String key, Object defaultValue) {
        return registerValue(key, defaultValue, ConfigSynchronization.UNIVERSAL, false);
    }
    
    public ConfigHolderDynamic registerValue(String key, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
        if (ConfigTypeConveration.has(defaultValue.getClass()))
            throw new IllegalArgumentException("Only holder objects are allowed");
        if (key.contains(".") || key.contains("/"))
            throw new RuntimeException("Invalid key " + key + "");
        if (fields.containsKey(key))
            throw new RuntimeException("Key already registered " + key + "");
        
        synchronization = this.synchronization != ConfigSynchronization.UNIVERSAL ? this.synchronization : synchronization;
        fields.add(key, new ConfigKeyDynamic(key, ConfigTypeConveration.parseObject(this, synchronization, key, defaultValue), synchronization, requiresRestart));
        return this;
    }
    
    public void registerField(String key, Field field, Object object) {
        registerField(key, field, object, ConfigSynchronization.CLIENT, false);
    }
    
    public void registerField(String key, Field field, Object object, ConfigSynchronization synchronization, boolean requiresRestart) {
        synchronization = this.synchronization != ConfigSynchronization.UNIVERSAL ? this.synchronization : synchronization;
        
        if (!ConfigTypeConveration.has(field.getType()))
            throw new RuntimeException("Field cannot contain holder object, use register value instead");
        
        try {
            ConfigKeyDynamicField configKey = new ConfigKeyDynamicField(field, key, field.get(object), synchronization, requiresRestart, object);
            fields.add(key, configKey);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
    }
    
}
