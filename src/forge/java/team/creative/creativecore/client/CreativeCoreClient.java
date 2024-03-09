package team.creative.creativecore.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkConstants;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.client.render.model.CreativeBlockModel;
import team.creative.creativecore.client.render.model.CreativeItemModel;
import team.creative.creativecore.client.render.model.CreativeModelLoader;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.integration.ContainerScreenIntegration;
import team.creative.creativecore.common.gui.integration.GuiEventHandler;
import team.creative.creativecore.common.gui.integration.GuiScreenIntegration;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.registry.LocatedHandlerRegistry;

import static team.creative.creativecore.CreativeCore.LOGGER;

public class CreativeCoreClient {
    
    private static final Minecraft mc = Minecraft.getInstance();
    public static final LocatedHandlerRegistry<CreativeBlockModel> BLOCK_MODEL_TYPES = new LocatedHandlerRegistry<CreativeBlockModel>(null).allowOverwrite();
    public static final LocatedHandlerRegistry<CreativeItemModel> ITEM_MODEL_TYPES = new LocatedHandlerRegistry<CreativeItemModel>(null).allowOverwrite();
    
    private static final ItemColor ITEM_COLOR = (stack, tint) -> tint;
    
    public static void load(IEventBus bus) {
        bus.addListener(CreativeCoreClient::init);
        bus.addListener(CreativeCoreClient::modelEvent);
    }
    
    public static void registerClientConfig(String modid) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((a, b) -> {
            ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(modid);
            if (holder != null && !holder.isEmpty(Side.CLIENT))
                return new GuiScreenIntegration(new ConfigGuiLayer(holder, Side.CLIENT));
            return null;
        }));
    }
    
    public static void registerBlockModel(ResourceLocation location, CreativeBlockModel renderer) {
        BLOCK_MODEL_TYPES.register(location, renderer);
    }
    
    public static void registerItemModel(ResourceLocation location, CreativeItemModel renderer) {
        ITEM_MODEL_TYPES.register(location, renderer);
    }
    
    public static void registerItemColor(ItemColors colors, Item item) {
        colors.register(ITEM_COLOR, item);
    }
    
    public static float getFrameTime() {
        if (mc.isPaused())
            return 1.0F;
        return mc.getFrameTime();
    }
    
    @SubscribeEvent
    public static void commands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder<CommandSourceStack>) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("cmdclientconfig")).executes((x) -> {
            try {
                GuiEventHandler.queueScreen(new GuiScreenIntegration(new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Side.CLIENT)));
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return 1;
        }));
    }
    
    public static void init(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(CreativeCoreClient.class);
        MinecraftForge.EVENT_BUS.register(GuiEventHandler.class);
        ModLoadingContext.get()
                .registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        GuiStyle.reload();
        Minecraft minecraft = Minecraft.getInstance();
        ReloadableResourceManager reloadableResourceManager = (ReloadableResourceManager) minecraft.getResourceManager();
        
        reloadableResourceManager.registerReloadListener(new SimplePreparableReloadListener() {
            
            @Override
            protected Object prepare(ResourceManager p_10796_, ProfilerFiller p_10797_) {
                return GuiStyle.class; // No idea
            }
            
            @Override
            protected void apply(Object p_10793_, ResourceManager p_10794_, ProfilerFiller p_10795_) {
                GuiStyle.reload();
            }
        });
        
        MenuScreens.register(CreativeCore.GUI_CONTAINER, new ScreenConstructor<ContainerIntegration, ContainerScreenIntegration>() {
            
            @Override
            public ContainerScreenIntegration create(ContainerIntegration container, Inventory inventory, Component p_create_3_) {
                return new ContainerScreenIntegration(container, inventory);
            }
        });
    }
    
    public static void modelEvent(RegisterGeometryLoaders event) {
        event.register("rendered", new CreativeModelLoader());
    }
    
    @SubscribeEvent
    public static void clientTick(ClientTickEvent event) {
        if (event.phase == Phase.START && Minecraft.getInstance().screen instanceof IScaleableGuiScreen gui)
            gui.clientTick();
    }
}
