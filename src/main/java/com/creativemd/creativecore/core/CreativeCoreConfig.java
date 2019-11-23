package com.creativemd.creativecore.core;

import com.creativemd.creativecore.CreativeCore;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = CreativeCore.modid, category = "")
@Mod.EventBusSubscriber
public class CreativeCoreConfig {
	
	@Config.Name("rendering")
	public static Rendering rendering = new Rendering();
	
	public static class Rendering {
		
		@Config.Name("useStencil")
		@Config.RequiresMcRestart
		@Config.Comment("Guis won't work properly with it disabled")
		public boolean useStencil = true;
		
	}
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(CreativeCore.modid)) {
			ConfigManager.sync(CreativeCore.modid, Config.Type.INSTANCE);
		}
	}
	
}
