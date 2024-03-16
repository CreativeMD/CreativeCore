package team.creative.creativecore;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
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
import team.creative.creativecore.common.gui.packet.ControlSyncPacket;
import team.creative.creativecore.common.gui.packet.ImmediateItemStackPacket;
import team.creative.creativecore.common.gui.packet.LayerClosePacket;
import team.creative.creativecore.common.gui.packet.OpenGuiPacket;
import team.creative.creativecore.common.gui.packet.SyncPacket;
import team.creative.creativecore.common.loader.ForgeLoaderUtils;
import team.creative.creativecore.common.loader.ILoaderUtils;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.creativecore.common.util.argument.StringArrayArgumentType;

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
    
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MODID);
    public static final Supplier<SingletonArgumentInfo<StringArrayArgumentType>> STRING_ARRAY_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("string_array",
        () -> ArgumentTypeInfos.registerByClass(StringArrayArgumentType.class, SingletonArgumentInfo.contextFree(StringArrayArgumentType::stringArray)));
    
    public CreativeCore() {
        ICreativeLoader loader = CreativeCore.loader();
        loader.registerDisplayTest(() -> loader.ignoreServerNetworkConstant(), (a, b) -> true);
        
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerMenus);
        COMMAND_ARGUMENT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        
        NeoForge.EVENT_BUS.addListener(this::server);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreativeCoreClient.load(FMLJavaModLoadingContext.get().getModEventBus()));
        
        GUI_CONTAINER = new MenuType<>(null, FeatureFlags.VANILLA_SET) {
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
    
    public void registerMenus(RegisterEvent event) {
        event.register(Registries.MENU, (x) -> x.register("container", GUI_CONTAINER));
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
