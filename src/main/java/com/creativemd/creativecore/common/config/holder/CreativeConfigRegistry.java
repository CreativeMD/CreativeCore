package com.creativemd.creativecore.common.config.holder;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;

import net.minecraftforge.fml.relauncher.Side;

public class CreativeConfigRegistry extends ConfigHolderDynamic {
	
	public CreativeConfigRegistry() {
		super();
	}
	
	public static final CreativeConfigRegistry ROOT = new CreativeConfigRegistry();
	
	@Override
	public ConfigHolderDynamic registerValue(String key, Object defaultValue) {
		throw new RuntimeException("Registry cannot contain values");
	}
	
	@Override
	public ConfigHolderDynamic registerValue(String key, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
		throw new RuntimeException("Registry cannot contain values");
	}
	
	public ICreativeConfigHolder followPath(String[] path) {
		ICreativeConfigHolder current = this;
		for (int i = 0; i < path.length; i++) {
			Object object = current.get(path[i]);
			if (object instanceof ICreativeConfigHolder)
				current = (ICreativeConfigHolder) object;
			else
				return null;
		}
		
		return this;
	}
	
	public static void load(String modid, Side side) {
		CreativeCore.configHandler.load(modid, side);
	}
	
}
