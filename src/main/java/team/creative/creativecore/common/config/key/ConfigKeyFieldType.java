package team.creative.creativecore.common.config.key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.IConfigObject;
import team.creative.creativecore.common.config.api.ICreativeConfig;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.field.ConfigField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class ConfigKeyFieldType extends ConfigKeyField {
    
    public final ConfigTypeConveration converation;
    public final Object defaultValue;
    
    public ConfigKeyFieldType(ConfigField field, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
        super(field, name, synchronization, requiresRestart);
        this.converation = ConfigTypeConveration.get(field.getType());
        this.defaultValue = defaultValue;
    }
    
    private void set(Object object, Side side) {
        if (!(defaultValue instanceof IConfigObject))
            field.set(converation.set(this, object));
    }
    
    @Override
    protected boolean checkEqual(Object one, Object two) {
        return converation.areEqual(one, two, this);
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
    public boolean isDefault(Side side) {
        if (defaultValue instanceof IConfigObject c)
            return c.isDefault(side);
        return checkEqual(defaultValue, get());
    }
    
    @Override
    public boolean isDefault(Object value, Side side) {
        if (defaultValue instanceof IConfigObject c)
            return c.isDefault(side);
        return checkEqual(defaultValue, value);
    }
    
    @Override
    public void restoreDefault(Side side, boolean ignoreRestart) {
        if (defaultValue instanceof IConfigObject c)
            c.restoreDefault(side, ignoreRestart);
        else
            set(defaultValue, side);
    }
    
    @Override
    public void read(HolderLookup.Provider provider, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side) {
        set(converation.readElement(provider, defaultValue, loadDefault, ignoreRestart, element, side, this), side);
    }
    
    @Override
    public JsonElement write(HolderLookup.Provider provider, boolean saveDefault, boolean ignoreRestart, Side side) {
        return converation.writeElement(provider, get(), saveDefault, ignoreRestart, side, this);
    }
    
    @Override
    public Object copy(HolderLookup.Provider provider, Side side) {
        return converation.readElement(provider, defaultValue, false, true, write(provider, false, true, side), side, this);
    }
    
    @Override
    public boolean isFolder() {
        return false;
    }
    
    @Override
    public ICreativeConfigHolder holder() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void triggerConfigured(Side side) {
        if (get() instanceof ICreativeConfig c)
            c.configured(side);
    }
    
}