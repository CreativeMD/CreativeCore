package team.creative.creativecore;

import java.util.OptionalLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
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
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.event.ConfigEventHandler;
import team.creative.creativecore.common.config.gui.ClientSyncGuiLayer;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.sync.ConfigurationChangePacket;
import team.creative.creativecore.common.config.sync.ConfigurationClientPacket;
import team.creative.creativecore.common.config.sync.ConfigurationPacket;
import team.creative.creativecore.common.gui.handler.GuiHandler;
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
    
    public static final String MODID = "creativecore";
    public static final Logger LOGGER = LogManager.getLogger(CreativeCore.MODID);
    public static final CreativeCoreConfig CONFIG = new CreativeCoreConfig();
    public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER, new ResourceLocation(CreativeCore.MODID, "main"));
    public static ConfigEventHandler CONFIG_HANDLER;
    
    public static final ResourceLocation FAKE_WORLD_LOCATION = new ResourceLocation(MODID, "fake");
    public static DimensionType FAKE_DIMENSION;
    public static ResourceKey<Level> FAKE_DIMENSION_NAME = ResourceKey.create(Registry.DIMENSION_REGISTRY, FAKE_WORLD_LOCATION);
    public static MenuType<ContainerIntegration> GUI_CONTAINER;
    
    public CreativeCore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(MenuType.class, this::registerMenus);
        
        MinecraftForge.EVENT_BUS.addListener(this::server);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(CreativeCoreClient::modelEvent));
        
        GuiHandler.register("clientconfig", (player, nbt) -> new ClientSyncGuiLayer(CreativeConfigRegistry.ROOT));
        GuiHandler.register("config", (player, nbt) -> new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Dist.DEDICATED_SERVER));
        
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
        GUI_CONTAINER.setRegistryName("container");
    }
    
    public void registerMenus(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(GUI_CONTAINER);
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
            GuiHandler.openGui("config", new CompoundTag(), x.getSource().getPlayerOrException());
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
        FAKE_DIMENSION = DimensionType.create(OptionalLong.empty(), true, false, false, false, 1, false, true, true, false, false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD
                .getName(), DimensionType.OVERWORLD_EFFECTS, 0.0F);
        
        ArgumentTypes.register("names", StringArrayArgumentType.class, new EmptyArgumentSerializer<>(() -> StringArrayArgumentType.stringArray()));
        
        GuiHandler.register("item", (player, nbt) -> {
            InteractionHand hand = nbt.getBoolean("main_hand") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof GuiHandler)
                return ((GuiHandler) stack.getItem()).create(player, nbt);
            return null;
        });
    }
    
}
