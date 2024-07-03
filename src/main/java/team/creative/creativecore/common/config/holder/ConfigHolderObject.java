package team.creative.creativecore.common.config.holder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.api.ICreativeConfig;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.key.ConfigKeyField;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class ConfigHolderObject extends ConfigHolder<ConfigKeyField> {
    
    public static ConfigHolderObject createUnrelated(Side side, Object value) {
        return createUnrelated(side, value, value);
    }
    
    public static ConfigHolderObject createUnrelated(Side side, Object value, Object defaultReference) {
        return new ConfigHolderObject(ConfigTypeConveration.FAKE_PARENT, side
                .isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, "", value, defaultReference);
    }
    
    private static List<Field> collectFields(Class clazz, List<Field> fields) {
        if (clazz.getSuperclass() != Object.class)
            collectFields(clazz.getSuperclass(), fields);
        
        Field[] declaredFields = clazz.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++)
            if (Modifier.isPublic(declaredFields[i].getModifiers()))
                fields.add(declaredFields[i]);
        return fields;
    }
    
    public final Object object;
    
    public ConfigHolderObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object) {
        this(parent, synchronization, key, object, object);
    }
    
    public ConfigHolderObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object, Object defaultReference) {
        super(parent, key, synchronization);
        this.object = object;
        List<Field> fields = collectFields(object.getClass(), new ArrayList<>());
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (field.isAnnotationPresent(CreativeConfig.class))
                try {
                    CreativeConfig config = field.getAnnotation(CreativeConfig.class);
                    String name;
                    if (config.name().isEmpty())
                        name = field.getName();
                    else
                        name = config.name();
                    ConfigSynchronization fieldSync = synchronization != ConfigSynchronization.UNIVERSAL ? synchronization : config.type();
                    this.fields.add(name, ConfigKeyField.of(this, field, name, field.get(defaultReference), fieldSync, config.requiresRestart(), object));
                } catch (IllegalArgumentException | IllegalAccessException e) {}
        }
    }
    
    @Override
    public void restoreDefault(Side side, boolean ignoreRestart) {
        super.restoreDefault(side, ignoreRestart);
        configured(side);
    }
    
    @Override
    public void load(HolderLookup.Provider provider, boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side) {
        super.load(provider, loadDefault, ignoreRestart, json, side);
        configured(side);
    }
    
    @Override
    public void configured(Side side) {
        if (object instanceof ICreativeConfig)
            ((ICreativeConfig) object).configured(side);
    }
    
}
