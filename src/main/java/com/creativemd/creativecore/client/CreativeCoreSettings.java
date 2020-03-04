package com.creativemd.creativecore.client;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.ConfigModGuiFactory;

public class CreativeCoreSettings extends ConfigModGuiFactory {
	
	@Override
	public String modid() {
		return CreativeCore.modid;
	}
	
}
