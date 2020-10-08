package team.creative.creativecore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.event.ConfigEventHandler;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.config.sync.ConfigurationClientPacket;
import team.creative.creativecore.common.config.sync.ConfigurationPacket;
import team.creative.creativecore.common.network.CreativeNetwork;

@Mod(value = CreativeCore.MODID)
public class CreativeCore {
	
	public static final String MODID = "creativecore";
	public static final Logger LOGGER = LogManager.getLogger(CreativeCore.MODID);
	public static final CreativeCoreConfig CONFIG = new CreativeCoreConfig();
	public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER, new ResourceLocation(CreativeCore.MODID, "main"));
	public static ConfigEventHandler CONFIG_HANDLER;
	
	public CreativeCore() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client));
	}
	
	@OnlyIn(value = Dist.CLIENT)
	private void client(final FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(CreativeCoreClient.class);
		CreativeCoreClient.init(event);
		
	}
	
	private void init(final FMLCommonSetupEvent event) {
		NETWORK.registerType(ConfigurationChangePacket.class);
		NETWORK.registerType(ConfigurationClientPacket.class);
		NETWORK.registerType(ConfigurationPacket.class);
		CONFIG_HANDLER = new ConfigEventHandler(FMLPaths.CONFIGDIR.get().toFile(), LOGGER);
	}
	
}
