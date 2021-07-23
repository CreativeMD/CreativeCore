package team.creative.creativecore;

import java.util.OptionalLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.FuzzyOffsetConstantColumnBiomeZoomer;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
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

@Mod(value = CreativeCore.MODID)
public class CreativeCore {
    
    public static final String MODID = "creativecore";
    public static final Logger LOGGER = LogManager.getLogger(CreativeCore.MODID);
    public static final CreativeCoreConfig CONFIG = new CreativeCoreConfig();
    public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER, new ResourceLocation(CreativeCore.MODID, "main"));
    public static ConfigEventHandler CONFIG_HANDLER;
    
    public static final ResourceLocation FAKE_WORLD_LOCATION = new ResourceLocation(MODID, "fake");
    public static DimensionType FAKE_DIMENSION;
    public static ResourceKey<Level> FAKE_DIMENSION_NAME = ResourceKey.create(Registry.DIMENSION_REGISTRY, FAKE_WORLD_LOCATION);
    
    public CreativeCore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        MinecraftForge.EVENT_BUS.addListener(this::server);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client));
        GuiContainerHandler.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        
        GuiContainerHandler.registerGuiHandler("clientconfig", new GuiHandlerPlayer() {
            
            @Override
            public GuiLayer create(Player player) {
                return new ClientSyncGuiLayer(CreativeConfigRegistry.ROOT);
            }
        });
        GuiContainerHandler.registerGuiHandler("config", new GuiHandlerPlayer() {
            
            @Override
            public GuiLayer create(Player player) {
                return new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Dist.DEDICATED_SERVER);
            }
        });
    }
    
    @OnlyIn(Dist.CLIENT)
    private void client(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(CreativeCoreClient.class);
        CreativeCoreClient.init(event);
        ModLoadingContext.get()
                .registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
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
        FAKE_DIMENSION = DimensionType.create(OptionalLong
                .empty(), true, false, false, false, 1, false, true, true, false, false, 0, 256, 256, FuzzyOffsetConstantColumnBiomeZoomer.INSTANCE, BlockTags.INFINIBURN_OVERWORLD
                        .getName(), DimensionType.OVERWORLD_EFFECTS, 0.0F);
        
    }
    
}
