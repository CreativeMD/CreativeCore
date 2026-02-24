package team.creative.creativecore.common.config.holder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.api.ICreativeConfig;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.core.ICreativeRegistry;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class ConfigHolderObject extends ConfigHolder<ConfigKey> {
    
    public static ConfigHolderObject createUnrelated(ICreativeRegistry registry, Side side, Object value) {
        return createUnrelated(registry, side, value, value);
    }
    
    public static ConfigHolderObject createUnrelated(ICreativeRegistry registry, Side side, Object value, Object defaultReference) {
        return new ConfigHolderObject(ConfigTypeConveration.FAKE_PARENT, side
                .isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, registry, "", value, defaultReference);
    }
    
    public static List<Field> collectFields(Class clazz, List<Field> fields, ICreativeRegistry registry) {
        if (clazz.getSuperclass() != Object.class)
            collectFields(clazz.getSuperclass(), fields, registry);
        
        Field[] declaredFields = clazz.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++)
            if (registry.is(declaredFields[i]))
                fields.add(declaredFields[i]);
        return fields;
    }
    
    public final Object object;
    private final ICreativeRegistry registry;
    
    public ConfigHolderObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object) {
        this(parent, synchronization, key, object, object);
    }
    
    public ConfigHolderObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object, Object defaultReference) {
        this(parent, synchronization, parent.getRegistry(), key, object, defaultReference);
    }
    
    private ConfigHolderObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, ICreativeRegistry registry, String key, Object object, Object defaultReference) {
        super(parent, key, synchronization);
        this.object = object;
        this.registry = registry;
        List<Field> fields = collectFields(object.getClass(), new ArrayList<>(), registry);
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String name = field.getName();
            ConfigSynchronization fieldSync = synchronization;
            boolean requiresRestart = false;
            boolean hideFromGUI = false;
            if (field.isAnnotationPresent(CreativeConfig.class)) {
                CreativeConfig config = field.getAnnotation(CreativeConfig.class);
                if (!config.name().isEmpty())
                    name = config.name();
                fieldSync = synchronization != ConfigSynchronization.UNIVERSAL ? synchronization : config.type();
                requiresRestart = config.requiresRestart();
                hideFromGUI = config.hideFromGUI();
            }
            try {
                var value = field.get(defaultReference);
                if (field.getType() == ConfigHolderDynamic.class && value == null)
                    field.set(defaultReference, value = new ConfigHolderDynamic(this, name, fieldSync));
                this.fields.add(name, ConfigKey.of(this, field, name, value, fieldSync, requiresRestart, hideFromGUI, object));
            } catch (IllegalArgumentException | IllegalAccessException e) {}
        }
    }
    
    @Override
    public ICreativeRegistry getRegistry() {
        return registry;
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
        if (object instanceof ICreativeConfig c)
            c.configured(side);
    }
    
}
