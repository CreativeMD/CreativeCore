package com.creativemd.creativecore.common.config.holder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.config.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject.ConfigKeyField;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.relauncher.Side;

public class ConfigHolderObject extends ConfigHolder<ConfigKeyField> {
	
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
					ConfigKeyField fieldKey = new ConfigKeyField(field, config.name(), ConfigTypeConveration.parseObject(this, fieldSync, name, field.get(object)), fieldSync, config.requiresRestart());
					this.fields.add(name, fieldKey);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					
				}
		}
	}
	
	@Override
	public void load(boolean loadDefault, JsonObject json, Side side) {
		super.load(loadDefault, json, side);
		if (object instanceof ICreativeConfig)
			((ICreativeConfig) object).configured();
	}
	
	@Override
	protected void reset(Side side) {
		super.reset(side);
		if (object instanceof ICreativeConfig)
			((ICreativeConfig) object).configured();
	}
	
	public class ConfigKeyField extends ConfigKey {
		
		public final Field field;
		public final ConfigTypeConveration converation;
		
		public ConfigKeyField(Field field, String configName, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
			super(field.getName(), configName, defaultValue, synchronization, requiresRestart);
			this.field = field;
			if (defaultValue instanceof ICreativeConfigHolder)
				this.converation = null;
			else
				this.converation = ConfigTypeConveration.get(field.getType());
		}
		
		@Override
		public void set(Object object) {
			try {
				if (!(defaultValue instanceof ICreativeConfigHolder))
					field.set(ConfigHolderObject.this.object, converation.set(this, object));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public Object get() {
			try {
				if (defaultValue instanceof ICreativeConfigHolder)
					return defaultValue;
				return field.get(ConfigHolderObject.this.object);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		protected boolean checkEqual(Object one, Object two) {
			if (converation != null)
				return converation.areEqual(one, two);
			return super.checkEqual(one, two);
		}
		
		public ConfigHolderObject getHolder() {
			return ConfigHolderObject.this;
		}
	}
	
}
