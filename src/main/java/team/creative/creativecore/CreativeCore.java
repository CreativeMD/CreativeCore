package team.creative.creativecore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import team.creative.creativecore.client.CreativeCoreClient;

@Mod(value = CreativeCore.MODID)
public class CreativeCore {
	
	public static final String MODID = "creativecore";
	public static final Logger LOGGER = LogManager.getLogger(CreativeCore.MODID);
	
	public CreativeCore() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);
	}
	
	private void client(final FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(CreativeCoreClient.class);
		CreativeCoreClient.init(event);
	}
	
}
