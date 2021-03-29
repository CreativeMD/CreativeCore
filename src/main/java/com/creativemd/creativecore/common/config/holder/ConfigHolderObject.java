package com.creativemd.creativecore.common.config.holder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;
import com.creativemd.creativecore.common.config.converation.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject.ConfigKeyFieldObject;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.relauncher.Side;

public class ConfigHolderObject extends ConfigHolder<ConfigKeyFieldObject> {
    
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
                    ConfigKeyFieldObject fieldKey = new ConfigKeyFieldObject(field, name, ConfigTypeConveration.parseObject(this, fieldSync, name, field.get(object)), fieldSync, config
                        .requiresRestart());
                    this.fields.add(name, fieldKey);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    
                }
        }
    }
    
    @Override
    public void restoreDefault(Side side, boolean ignoreRestart) {
        super.restoreDefault(side, ignoreRestart);
        if (object instanceof ICreativeConfig)
            ((ICreativeConfig) object).configured();
    }
    
    @Override
    public void load(boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side) {
        super.load(loadDefault, ignoreRestart, json, side);
        if (object instanceof ICreativeConfig)
            ((ICreativeConfig) object).configured();
    }
    
    public class ConfigKeyFieldObject extends ConfigKeyField {
        
        public ConfigKeyFieldObject(Field field, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
            super(field, name, defaultValue, synchronization, requiresRestart);
        }
        
        @Override
        public Object getParent() {
            return getHolder().object;
        }
        
        public ConfigHolderObject getHolder() {
            return ConfigHolderObject.this;
        }
    }
    
}
