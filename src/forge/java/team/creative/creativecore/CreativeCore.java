package team.creative.creativecore;

import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.event.ConfigEventHandler;
import team.creative.creativecore.common.config.gui.ClientSyncGuiLayer;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.config.sync.ConfigurationClientPacket;
import team.creative.creativecore.common.config.sync.ConfigurationPacket;
import team.creative.creativecore.common.gui.creator.GuiCreator;
import team.creative.creativecore.common.gui.creator.GuiCreator.GuiCreatorBasic;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.packet.*;
import team.creative.creativecore.common.loader.ForgeLoaderUtils;
import team.creative.creativecore.common.loader.ILoaderUtils;
import team.creative.creativecore.common.network.CreativeNetwork;

@Mod(CreativeCore.MODID)
public class CreativeCore {
    
    private static final ICreativeLoader LOADER = new CreativeForgeLoader();
    private static final ILoaderUtils UTILS = new ForgeLoaderUtils();
    public static final String MODID = "creativecore";
    public static final Logger LOGGER = LogManager.getLogger(CreativeCore.MODID);
    public static final CreativeCoreConfig CONFIG = new CreativeCoreConfig();
    public static final CreativeNetwork NETWORK = new CreativeNetwork(1, LOGGER, new ResourceLocation(CreativeCore.MODID, "main"));
    public static ConfigEventHandler CONFIG_HANDLER;
    
    public static MenuType<ContainerIntegration> GUI_CONTAINER;
    
    public static final GuiCreatorBasic CONFIG_OPEN = GuiCreator.register("config",
        new GuiCreatorBasic((player, nbt) -> new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Side.SERVER)));
    public static final GuiCreatorBasic CONFIG_CLIENT_SYNC_OPEN = GuiCreator.register("clientconfig",
        new GuiCreatorBasic((player, nbt) -> new ClientSyncGuiLayer(CreativeConfigRegistry.ROOT)));

    public CreativeCore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerMenus);
        
        MinecraftForge.EVENT_BUS.addListener(this::server);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreativeCoreClient.load(FMLJavaModLoadingContext.get().getModEventBus()));
        
        GUI_CONTAINER = new MenuType<>(null) {
            @Override
            public ContainerIntegration create(int windowId, Inventory playerInv, net.minecraft.network.FriendlyByteBuf extraData) {
                return new ContainerIntegration(this, windowId, playerInv.player);
            }
            
            @Override
            public ContainerIntegration create(int windowId, Inventory playerInv) {
                return new ContainerIntegration(this, windowId, playerInv.player);
            }
        };
    }
    
    public void registerMenus(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(GUI_CONTAINER);
    }
    
    private void server(final ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(Commands.literal("cmdconfig").executes(x -> {
            CONFIG_OPEN.open(new CompoundTag(), x.getSource().getPlayerOrException());
            return 0;
        }));
    }
    
    private void init(final FMLCommonSetupEvent event) {
        NETWORK.registerType(ConfigurationChangePacket.class, ConfigurationChangePacket::new);
        NETWORK.registerType(ConfigurationClientPacket.class, ConfigurationClientPacket::new);
        NETWORK.registerType(ConfigurationPacket.class, ConfigurationPacket::new);
        NETWORK.registerType(LayerClosePacket.class, LayerClosePacket::new);
        NETWORK.registerType(OpenGuiPacket.class, OpenGuiPacket::new);
        NETWORK.registerType(ControlSyncPacket.class, ControlSyncPacket::new);
        NETWORK.registerType(SyncPacket.class, SyncPacket::new);
        NETWORK.registerType(ImmediateItemStackPacket.class, ImmediateItemStackPacket::new);
        CONFIG_HANDLER = new ConfigEventHandler(FMLPaths.CONFIGDIR.get().toFile(), LOGGER);
        
        LOADER.loadCommon();
    }
    
    public static ICreativeLoader loader() {
        return LOADER;
    }
    
    public static ILoaderUtils utils() {
        return UTILS;
    }
}
