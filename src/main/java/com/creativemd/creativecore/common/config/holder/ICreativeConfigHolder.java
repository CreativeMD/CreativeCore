package com.creativemd.creativecore.common.config.holder;

import java.util.Collection;

import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.relauncher.Side;

public interface ICreativeConfigHolder {
	
	public ICreativeConfigHolder parent();
	
	public default String getName() {
		return path()[path().length - 1];
	}
	
	public String[] path();
	
	public Collection<? extends ConfigKey> fields();
	
	public Collection<String> names();
	
	public Object get(String key);
	
	public ConfigKey getField(String key);
	
	public boolean isDefault(Side side);
	
	public void restoreDefault(Side side, boolean ignoreRestart);
	
	public void load(boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side);
	
	public JsonObject save(boolean saveDefault, boolean ignoreRestart, Side side);
	
	public boolean isEmpty(Side side);
	
	public boolean isEmptyWithoutForce(Side side);
	
	public ConfigSynchronization synchronization();
	
}
