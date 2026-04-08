package team.creative.creativecore.common.config.key;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.core.IConfigRegistry;
import team.creative.creativecore.common.config.field.ConfigField;
import team.creative.creativecore.common.config.field.ConfigFieldTyped;
import team.creative.creativecore.common.config.field.ConfigFieldWrapper;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ConfigHolder;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public abstract class ConfigKey {
    
    public static ConfigKey of(ICreativeConfigHolder parentHolder, Field field, String name, Object defaultValue, ConfigSynchronization sync, boolean requiresRestart,
            boolean hideFromGUI, Object parent) {
        return of(parentHolder, new ConfigFieldWrapper(parent, field), name, defaultValue, sync, requiresRestart, hideFromGUI);
    }
    
    public static ConfigKey of(ICreativeConfigHolder parent, ConfigField field, String name, Object defaultValue, ConfigSynchronization sync, boolean requiresRestart,
            boolean hideFromGUI) {
        if (ConfigTypeConveration.has(field.getType()))
            return new ConfigKeyType(field, name, defaultValue, sync, requiresRestart, hideFromGUI, parent.getRegistry());
        if (defaultValue instanceof ConfigHolder holder)
            return new ConfigKeyHolder(holder, field, name, sync, requiresRestart, hideFromGUI);
        return new ConfigKeyHolder(new ConfigHolderObject(parent, sync, name, defaultValue), field, name, sync, requiresRestart, hideFromGUI);
    }
    
    public static ConfigKey ofGenericType(ConfigKey key, Side side) {
        Type type = ConfigTypeConveration.getGenericType(key);
        Class clazz = type instanceof ParameterizedType p ? (Class) p.getRawType() : (Class) type;
        ConfigField field = new ConfigFieldTyped(null, type, clazz);
        return ofType(key, field, side);
    }
    
    public static ConfigKey ofArrayType(ConfigKey key, Side side) {
        Type type = ConfigTypeConveration.getGenericType(key);
        Class clazz = key.field().getType().getComponentType();
        ConfigField field = new ConfigFieldTyped(null, type, clazz);
        return ofType(key, field, side);
    }
    
    public static ConfigKey ofType(ConfigKey key, ConfigField field, Side side) {
        field.set(ConfigTypeConveration.createObject(field));
        if (ConfigTypeConveration.has(field.getType()))
            return new ConfigKeyType(field, "", ConfigTypeConveration.createObject(field), ConfigSynchronization.UNIVERSAL, false, false, key.getRegistry());
        return new ConfigKeyHolder(ConfigHolderObject.createUnrelated(key.getRegistry(), side, field.get()), field, "", ConfigSynchronization.UNIVERSAL, false, false);
    }
    
    public final String name;
    public final ConfigSynchronization synchronization;
    public final boolean requiresRestart;
    public final boolean hideFromGUI;
    
    public boolean forceSynchronization;
    protected final ConfigField field;
    
    public ConfigKey(ConfigField field, String name, ConfigSynchronization synchronization, boolean requiresRestart, boolean hideFromGUI) {
        this.synchronization = synchronization;
        this.requiresRestart = requiresRestart;
        this.hideFromGUI = hideFromGUI;
        this.name = name;
        this.field = field;
    }
    
    public Object get() {
        return field.get();
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfigKey c)
            return c.name.equals(this.name);
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    protected boolean checkEqual(Object one, Object two, Side side) {
        return one.equals(two);
    }
    
    public abstract boolean isFolder();
    
    public abstract ICreativeConfigHolder holder();
    
    public abstract ConfigTypeConveration converation();
    
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
    
    public abstract void forceValue(Object object, Side side);
    
    public abstract void read(HolderLookup.Provider provider, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side);
    
    public abstract JsonElement write(HolderLookup.Provider provider, boolean saveDefault, boolean ignoreRestart, Side side);
    
    public abstract Object copy(HolderLookup.Provider provider, Side side);
    
    public abstract GuiConfigSubControl create(IGuiConfigParent configParent, String name, Side side);
    
    public abstract void load(IGuiConfigParent configParent, GuiConfigSubControl control, Side side);
    
    public abstract void save(GuiConfigSubControl control, IGuiConfigParent configParent, Side side);
    
    public ConfigField field() {
        return field;
    }
    
    public abstract IConfigRegistry getRegistry();
    
}