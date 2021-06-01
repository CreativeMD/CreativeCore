package team.creative.creativecore;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.event.ConfigEventHandler;
import team.creative.creativecore.common.config.gui.ClientSyncGuiLayer;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.config.sync.ConfigurationClientPacket;
import team.creative.creativecore.common.config.sync.ConfigurationPacket;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.handler.GuiContainerHandler;
import team.creative.creativecore.common.gui.handler.GuiContainerHandler.GuiHandlerPlayer;
import team.creative.creativecore.common.gui.integration.GuiEventHandler;
import team.creative.creativecore.common.gui.sync.LayerClosePacket;
import team.creative.creativecore.common.gui.sync.LayerOpenPacket;
import team.creative.creativecore.common.gui.sync.OpenGuiPacket;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.creativecore.common.world.EmptyFakeDimension;
import team.creative.creativecore.common.world.EmptyFakeForgeWorld;

@Mod(value = CreativeCore.MODID)
public class CreativeCore {
    
    public static final String MODID = "creativecore";
    public static final Logger LOGGER = LogManager.getLogger(CreativeCore.MODID);
    public static final CreativeCoreConfig CONFIG = new CreativeCoreConfig();
    public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER, new ResourceLocation(CreativeCore.MODID, "main"));
    public static ConfigEventHandler CONFIG_HANDLER;
    
    public static final ResourceLocation FAKE_WORLD_LOCATION = new ResourceLocation(MODID, "fake");
    public static EmptyFakeForgeWorld FAKE_FORGE_WORLD;
    public static EmptyFakeDimension FAKE_DIMENSION;
    public static RegistryKey<World> FAKE_DIMENSION_NAME = RegistryKey.create(Registry.DIMENSION_REGISTRY, FAKE_WORLD_LOCATION);
    
    public CreativeCore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        MinecraftForge.EVENT_BUS.addListener(this::server);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerDimensions);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client));
        GuiContainerHandler.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        
        GuiContainerHandler.registerGuiHandler("clientconfig", new GuiHandlerPlayer() {
            
            @Override
            public GuiLayer create(PlayerEntity player) {
                return new ClientSyncGuiLayer(CreativeConfigRegistry.ROOT);
            }
        });
        GuiContainerHandler.registerGuiHandler("config", new GuiHandlerPlayer() {
            
            @Override
            public GuiLayer create(PlayerEntity player) {
                return new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Dist.DEDICATED_SERVER);
            }
        });
    }
    
    @OnlyIn(value = Dist.CLIENT)
    private void client(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(CreativeCoreClient.class);
        CreativeCoreClient.init(event);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }
    
    private void server(final FMLServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(Commands.literal("cmdconfig").executes(x -> {
            GuiContainerHandler.openGui(x.getSource().getPlayerOrException(), "config");
            return 0;
        }));
    }
    
    private void init(final FMLCommonSetupEvent event) {
        NETWORK.registerType(ConfigurationChangePacket.class);
        NETWORK.registerType(ConfigurationClientPacket.class);
        NETWORK.registerType(ConfigurationPacket.class);
        NETWORK.registerType(LayerClosePacket.class);
        NETWORK.registerType(LayerOpenPacket.class);
        NETWORK.registerType(OpenGuiPacket.class);
        CONFIG_HANDLER = new ConfigEventHandler(FMLPaths.CONFIGDIR.get().toFile(), LOGGER);
        MinecraftForge.EVENT_BUS.register(GuiEventHandler.class);
        FAKE_DIMENSION = new EmptyFakeDimension();
        
    }
    
    public void registerDimensions(RegistryEvent.Register<ForgeWorldType> event) {
        event.getRegistry().register((FAKE_FORGE_WORLD = new EmptyFakeForgeWorld()).setRegistryName(FAKE_WORLD_LOCATION));
    }
    
}
