package team.creative.creativecore.common.config.key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.field.ConfigField;
import team.creative.creativecore.common.config.field.ConfigFieldWrapper;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public abstract class ConfigKeyField extends ConfigKey {
    
    public static ConfigKeyField of(ICreativeConfigHolder parentHolder, Field field, String name, Object defaultValue, ConfigSynchronization sync, boolean requiresRestart, Object parent) {
        return of(parentHolder, new ConfigFieldWrapper(parent, field), name, defaultValue, sync, requiresRestart);
    }
    
    public static ConfigKeyField of(ICreativeConfigHolder parent, ConfigField field, String name, Object defaultValue, ConfigSynchronization sync, boolean requiresRestart) {
        if (ConfigTypeConveration.has(field.getType()))
            return new ConfigKeyFieldType(field, name, defaultValue, sync, requiresRestart);
        return new ConfigKeyFieldHolder(new ConfigHolderObject(parent, sync, name, defaultValue), field, name, sync, requiresRestart);
    }
    
    public final String name;
    public final ConfigSynchronization synchronization;
    public final boolean requiresRestart;
    
    public boolean forceSynchronization;
    public final ConfigField field;
    
    public ConfigKeyField(ConfigField field, String name, ConfigSynchronization synchronization, boolean requiresRestart) {
        this.synchronization = synchronization;
        this.requiresRestart = requiresRestart;
        this.name = name;
        this.field = field;
    }
    
    @Override
    public Object get() {
        return field.get();
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }
    
    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }
    
    @Override
    public Class getType() {
        return field.getType();
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfigKeyField c)
            return c.name.equals(this.name);
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    protected boolean checkEqual(Object one, Object two) {
        return one.equals(two);
    }
    
    public abstract boolean isFolder();
    
    public abstract ICreativeConfigHolder holder();
    
    public boolean is(Side side) {
        if (isFolder())
            return synchronization.useFolder(forceSynchronization, side);
        return synchronization.useValue(forceSynchronization, side);
    }
    
    public boolean isWithoutForce(Side side) {
        if (isFolder())
            return synchronization.useFolder(false, side);
        return synchronization.useValue(false, side);
    }
    
    public abstract void triggerConfigured(Side side);
    
    public abstract boolean isDefault(Side side);
    
    public abstract boolean isDefault(Object value, Side side);
    
    public abstract void restoreDefault(Side side, boolean ignoreRestart);
    
}