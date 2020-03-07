package com.creativemd.creativecore.common.config.holder;

import java.util.Collection;

import com.creativemd.creativecore.common.config.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject.ConfigKeyField;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.relauncher.Side;

public abstract class ConfigHolder<T extends ConfigKey> implements ICreativeConfigHolder {
	
	public final ConfigSynchronization synchronization;
	public final ICreativeConfigHolder parent;
	public final String[] path;
	
	protected PairList<String, T> fields = new PairList<>();
	
	public ConfigHolder(ICreativeConfigHolder parent, String key, ConfigSynchronization synchronization) {
		this.parent = parent;
		this.path = ObjectArrays.concat(parent.path(), key);
		this.synchronization = synchronization;
	}
	
	ConfigHolder() {
		this.parent = null;
		this.path = new String[] {};
		this.synchronization = ConfigSynchronization.UNIVERSAL;
	}
	
	@Override
	public ConfigSynchronization synchronization() {
		return synchronization;
	}
	
	@Override
	public ICreativeConfigHolder parent() {
		return parent;
	}
	
	@Override
	public String[] path() {
		return path;
	}
	
	@Override
	public ConfigKey getField(String key) {
		return fields.getValue(key);
	}
	
	@Override
	public Collection<? extends ConfigKey> fields() {
		return fields.values();
	}
	
	@Override
	public Collection<String> names() {
		return fields.keys();
	}
	
	@Override
	public Object get(String key) {
		T field = fields.getValue(key);
		if (field != null)
			return field.get();
		return null;
	}
	
	@Override
	public boolean isEmpty(Side side) {
		for (int i = 0; i < fields.size(); i++) {
			T field = fields.get(i).value;
			if (field.get() instanceof ICreativeConfigHolder) {
				if (!((ICreativeConfigHolder) field.get()).isEmpty(side))
					return false;
			} else if (field.is(side))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean isEmptyWithoutForce(Side side) {
		for (int i = 0; i < fields.size(); i++) {
			T field = fields.get(i).value;
			if (field.get() instanceof ICreativeConfigHolder) {
				if (!((ICreativeConfigHolder) field.get()).isEmptyWithoutForce(side))
					return false;
			} else if (field.isWithoutForce(side))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean isDefault(Side side) {
		for (int i = 0; i < fields.size(); i++)
			if (fields.get(i).value.is(side) && !fields.get(i).value.isDefault(side))
				return false;
		return true;
	}
	
	@Override
	public void restoreDefault(Side side) {
		reset(side);
	}
	
	protected void reset(Side side) {
		for (int i = 0; i < fields.size(); i++)
			if (fields.get(i).value.is(side))
				fields.get(i).value.restoreDefault(side);
	}
	
	@Override
	public void load(boolean loadDefault, JsonObject json, Side side) {
		if (loadDefault)
			reset(side);
		for (int i = 0; i < fields.size(); i++) {
			T field = fields.get(i).value;
			if (field.is(side) && json.has(field.name))
				field.set(ConfigTypeConveration.read(field.getType(), field.getDefault(), loadDefault, json.get(field.name), side, field instanceof ConfigKeyField ? (ConfigKeyField) field : null));
		}
	}
	
	@Override
	public JsonObject save(boolean saveDefault, Side side) {
		JsonObject object = new JsonObject();
		for (int i = 0; i < fields.size(); i++) {
			T field = fields.get(i).value;
			if (field.is(side) && (saveDefault || !field.isDefault(side)))
				object.add(field.name, ConfigTypeConveration.write(field.getType(), field.get(), field.getDefault(), saveDefault, side, field instanceof ConfigKeyField ? (ConfigKeyField) field : null));
		}
		return object;
	}
	
}
