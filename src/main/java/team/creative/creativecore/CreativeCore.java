package team.creative.creativecore;

import java.util.OptionalLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.event.ConfigEventHandler;
import team.creative.creativecore.common.config.gui.ClientSyncGuiLayer;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.gui.GuiInfoStackButton;
import team.creative.creativecore.common.config.gui.GuiPlayerSelectorButton;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.config.sync.ConfigurationClientPacket;
import team.creative.creativecore.common.config.sync.ConfigurationPacket;
import team.creative.creativecore.common.gui.dialog.GuiDialogHandler;
import team.creative.creativecore.common.gui.handler.GuiCreator;
import team.creative.creativecore.common.gui.handler.GuiCreator.GuiCreatorBasic;
import team.creative.creativecore.common.gui.handler.GuiLayerHandler;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.integration.GuiEventHandler;
import team.creative.creativecore.common.gui.packet.ControlSyncPacket;
import team.creative.creativecore.common.gui.packet.LayerClosePacket;
import team.creative.creativecore.common.gui.packet.LayerOpenPacket;
import team.creative.creativecore.common.gui.packet.OpenGuiPacket;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.creativecore.common.util.argument.StringArrayArgumentType;

@Mod(value = CreativeCore.MODID)
public class CreativeCore {
    
    private static final ICreativeLoader LOADER = new CreativeForgeLoader();
    public static final String MODID = "creativecore";
    public static final Logger LOGGER = LogManager.getLogger(CreativeCore.MODID);
    public static final CreativeCoreConfig CONFIG = new CreativeCoreConfig();
    public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER, new ResourceLocation(CreativeCore.MODID, "main"));
    public static ConfigEventHandler CONFIG_HANDLER;
    
    public static final ResourceLocation FAKE_WORLD_LOCATION = new ResourceLocation(MODID, "fake");
    public static Holder<DimensionType> FAKE_DIMENSION;
    public static ResourceKey<Level> FAKE_DIMENSION_NAME = ResourceKey.create(Registry.DIMENSION_REGISTRY, FAKE_WORLD_LOCATION);
    public static MenuType<ContainerIntegration> GUI_CONTAINER;
    
    public static final GuiCreatorBasic CONFIG_OPEN = GuiCreator
            .register("config", new GuiCreatorBasic((player, nbt) -> new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Side.SERVER)));
    public static final GuiCreatorBasic CLIENT_CONFIG_OPEN = GuiCreator
            .register("clientconfig", new GuiCreatorBasic((player, nbt) -> new ClientSyncGuiLayer(CreativeConfigRegistry.ROOT)));
    
    public CreativeCore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerMenus);
        
        MinecraftForge.EVENT_BUS.addListener(this::server);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(CreativeCoreClient::modelEvent));
        
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
    
    public void registerMenus(RegisterEvent event) {
        event.register(Keys.CONTAINER_TYPES, (x) -> x.register("container", GUI_CONTAINER));
    }
    
    @OnlyIn(Dist.CLIENT)
    private void client(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(CreativeCoreClient.class);
        CreativeCoreClient.init(event);
        ModLoadingContext.get()
                .registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
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
        NETWORK.registerType(LayerOpenPacket.class, LayerOpenPacket::new);
        NETWORK.registerType(OpenGuiPacket.class, OpenGuiPacket::new);
        NETWORK.registerType(ControlSyncPacket.class, ControlSyncPacket::new);
        CONFIG_HANDLER = new ConfigEventHandler(FMLPaths.CONFIGDIR.get().toFile(), LOGGER);
        MinecraftForge.EVENT_BUS.register(GuiEventHandler.class);
        FAKE_DIMENSION = Holder.direct(new DimensionType(OptionalLong
                .empty(), true, false, false, false, 1, false, false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F, new DimensionType.MonsterSettings(false, false, UniformInt
                        .of(0, 0), 0)));
        
        ArgumentTypeInfos.registerByClass(StringArrayArgumentType.class, SingletonArgumentInfo.contextFree(() -> StringArrayArgumentType.stringArray()));
        
        GuiLayerHandler.REGISTRY.register("info", GuiInfoStackButton.INFO_LAYER);
        GuiLayerHandler.REGISTRY.register("player", GuiPlayerSelectorButton.PLAYER_LAYER);
        GuiLayerHandler.REGISTRY.register("dialog", GuiDialogHandler.DIALOG_HANDLER);
    }
    
    public static ICreativeLoader loader() {
        return LOADER;
    }
}
