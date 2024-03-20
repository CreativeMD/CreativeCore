package team.creative.creativecore.common.config.holder;

import java.lang.reflect.Field;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.IConfigObject;
import team.creative.creativecore.common.config.api.ICreativeConfig;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public abstract class ConfigKey {
    
    public final String name;
    public final ConfigSynchronization synchronization;
    public final boolean requiresRestart;
    
    protected final Object defaultValue;
    
    public boolean forceSynchronization;
    
    public ConfigKey(String fieldName, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
        this.synchronization = synchronization;
        this.requiresRestart = requiresRestart;
        if (name.isEmpty())
            this.name = fieldName;
        else
            this.name = name;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfigKey)
            return ((ConfigKey) obj).name.equals(this.name);
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public abstract void set(Object object, Side side);
    
    public abstract Object get();
    
    protected boolean checkEqual(Object one, Object two) {
        return one.equals(two);
    }
    
    public boolean isDefault(Side side) {
        if (defaultValue instanceof IConfigObject configObject)
            return configObject.isDefault(side);
        return checkEqual(defaultValue, get());
    }
    
    public boolean isDefault(Object value, Side side) {
        if (defaultValue instanceof IConfigObject configObject)
            return configObject.isDefault(side);
        return checkEqual(defaultValue, value);
    }
    
    public void restoreDefault(Side side, boolean ignoreRestart) {
        if (defaultValue instanceof IConfigObject configObject)
            configObject.restoreDefault(side, ignoreRestart);
        if (defaultValue instanceof ICreativeConfigHolder holder)
            holder.restoreDefault(side, ignoreRestart);
        else
            set(defaultValue, side);
    }
    
    public Object getDefault() {
        return defaultValue;
    }
    
    public Class getType() {
        return defaultValue.getClass();
    }
    
    public boolean is(Side side) {
        if (defaultValue instanceof ICreativeConfigHolder)
            return synchronization.useFolder(forceSynchronization, side);
        return synchronization.useValue(forceSynchronization, side);
    }
    
    public boolean isWithoutForce(Side side) {
        if (defaultValue instanceof ICreativeConfigHolder)
            return synchronization.useFolder(false, side);
        return synchronization.useValue(false, side);
    }
    
    public void triggerConfigured(Side side) {
        if (defaultValue instanceof ICreativeConfigHolder con)
            con.configured(side);
        else if (get() instanceof ICreativeConfig con)
            con.configured(side);
    }
    
    public static class ConfigKeyDynamic extends ConfigKey {
        
        private Object value;
        
        public ConfigKeyDynamic(String name, Object defaultValue, ConfigSynchronization type, boolean requiresRestart) {
            super(name, "", defaultValue, type, requiresRestart);
            if (defaultValue instanceof ICreativeConfigHolder)
                this.value = null;
            else
                this.value = defaultValue;
        }
        
        @Override
        public void set(Object object, Side side) {
            if (!(defaultValue instanceof ICreativeConfigHolder))
                this.value = object;
        }
        
        @Override
        public Object get() {
            if (defaultValue instanceof ICreativeConfigHolder)
                return defaultValue;
            return value;
        }
        
    }
    
    public static abstract class ConfigKeyField extends ConfigKey {
        
        public final Field field;
        public final ConfigTypeConveration converation;
        
        public ConfigKeyField(Field field, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
            super(name, name, defaultValue, synchronization, requiresRestart);
            this.field = field;
            if (defaultValue instanceof ICreativeConfigHolder)
                this.converation = null;
            else
                this.converation = ConfigTypeConveration.get(field.getType());
        }
        
        public abstract Object getParent();
        
        @Override
        public void set(Object object, Side side) {
            try {
                if (!(defaultValue instanceof ICreativeConfigHolder))
                    field.set(getParent(), converation.set(this, object));
                
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public Object get() {
            try {
                if (defaultValue instanceof ICreativeConfigHolder)
                    return defaultValue;
                return field.get(getParent());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        protected boolean checkEqual(Object one, Object two) {
            if (converation != null)
                return converation.areEqual(one, two, this);
            return super.checkEqual(one, two);
        }
        
    }
    
    public static class ConfigKeyDynamicField extends ConfigKeyField {
        
        public final Object parent;
        
        public ConfigKeyDynamicField(Field field, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart, Object parent) {
            super(field, name, defaultValue, synchronization, requiresRestart);
            this.parent = parent;
        }
        
        @Override
        public Object getParent() {
            return parent;
        }
        
    }
    
}
