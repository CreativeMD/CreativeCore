package team.creative.creativecore.common.config.holder;

import static team.creative.creativecore.CreativeCore.LOGGER;

import java.lang.reflect.Field;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.field.ConfigFieldDynamic;
import team.creative.creativecore.common.config.key.ConfigKeyField;
import team.creative.creativecore.common.config.key.ConfigKeyFieldHolder;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class ConfigHolderDynamic extends ConfigHolder<ConfigKeyField> {
    
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
            throw new RuntimeException("Invalid key " + key);
        if (fields.containsKey(key))
            throw new RuntimeException("Key already registered " + key);
        
        synchronization = this.synchronization != ConfigSynchronization.UNIVERSAL ? this.synchronization : synchronization;
        
        ConfigHolderDynamic holder = new ConfigHolderDynamic(this, key, synchronization);
        fields.add(key, new ConfigKeyFieldHolder(holder, new ConfigFieldDynamic(holder), key, synchronization, false));
        return holder;
    }
    
    public ConfigHolderDynamic registerValue(String key, Object defaultValue) {
        return registerValue(key, defaultValue, ConfigSynchronization.UNIVERSAL, false);
    }
    
    public ConfigHolderDynamic registerValue(String key, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
        if (ConfigTypeConveration.has(defaultValue.getClass()))
            throw new IllegalArgumentException("Only holder objects are allowed");
        if (key.contains(".") || key.contains("/"))
            throw new RuntimeException("Invalid key " + key);
        if (fields.containsKey(key))
            throw new RuntimeException("Key already registered " + key);
        
        synchronization = this.synchronization != ConfigSynchronization.UNIVERSAL ? this.synchronization : synchronization;
        fields.add(key, ConfigKeyField.of(this, new ConfigFieldDynamic(defaultValue), key, defaultValue, synchronization, requiresRestart));
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
            fields.add(key, ConfigKeyField.of(this, field, key, field.get(object), synchronization, requiresRestart, object));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error(e);
        }
        
    }
    
    @Override
    public void configured(Side side) {}
    
}
