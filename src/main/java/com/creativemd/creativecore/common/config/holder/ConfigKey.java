package com.creativemd.creativecore.common.config.holder;

import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;

import net.minecraftforge.fml.relauncher.Side;

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
	
	public abstract void set(Object object);
	
	public abstract Object get();
	
	protected boolean checkEqual(Object one, Object two) {
		return one.equals(two);
	}
	
	public boolean isDefault(Side side) {
		if (defaultValue instanceof ICreativeConfigHolder)
			return ((ICreativeConfigHolder) defaultValue).isDefault(side);
		return checkEqual(defaultValue, get());
	}
	
	public boolean isDefault(Object value, Side side) {
		if (defaultValue instanceof ICreativeConfigHolder)
			return ((ICreativeConfigHolder) defaultValue).isDefault(side);
		return checkEqual(defaultValue, value);
	}
	
	public void restoreDefault(Side side) {
		if (defaultValue instanceof ICreativeConfigHolder)
			((ICreativeConfigHolder) defaultValue).restoreDefault(side);
		else
			set(defaultValue);
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
		public void set(Object object) {
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
	
}
