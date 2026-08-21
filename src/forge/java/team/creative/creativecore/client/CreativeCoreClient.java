package team.creative.creativecore.client;

import static team.creative.creativecore.CreativeCore.LOGGER;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.client.render.model.box.CreativeUnbakedBoxItemLayer;
import team.creative.creativecore.client.render.model.box.ItemModelBox;
import team.creative.creativecore.client.render.model.empty.CreativeModelEmptyLoader;
import team.creative.creativecore.client.render.model.layer.CreativeModelLayerLoader;
import team.creative.creativecore.client.render.model.preview.CreativeUnbakedPreviewItemLayer;
import team.creative.creativecore.client.render.model.preview.ItemModelPreview;
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

public class CreativeCoreClient {
    
    private static final Minecraft mc = Minecraft.getInstance();
    
    public static final LocatedHandlerRegistry<ItemModelBox> BOX_MODEL_TYPES = new LocatedHandlerRegistry<ItemModelBox>(null).allowOverwrite();
    public static final LocatedHandlerRegistry<ItemModelPreview> PREVIEW_MODEL_TYPES = new LocatedHandlerRegistry<ItemModelPreview>(null).allowOverwrite();
    
    private static final ItemColor ITEM_COLOR = (stack, tint) -> tint;
    
    public static void load(IEventBus bus) {
        bus.addListener(CreativeCoreClient::init);
        bus.addListener(CreativeCoreClient::modelEvent);
        bus.addListener(CreativeCoreClient::screenEvent);
        
        CreativeModelLayerLoader.REGISTRY.register(CreativeCore.MODID + ":box", CreativeUnbakedBoxItemLayer.class);
        CreativeModelLayerLoader.REGISTRY.register(CreativeCore.MODID + ":preview", CreativeUnbakedPreviewItemLayer.class);
    }
    
    public static void registerClientConfig(String modid) {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ((a, b) -> {
            ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(modid);
            if (holder != null && !holder.isEmpty(Side.CLIENT))
                return new GuiScreenIntegration(new ConfigGuiLayer(holder, Side.CLIENT));
            return null;
        }));
    }
    
    public static void registerItemBoxModel(ResourceLocation location, ItemModelBox renderer) {
        BOX_MODEL_TYPES.register(location, renderer);
    }
    
    public static void registerItemPreviewModel(ResourceLocation location, ItemModelPreview renderer) {
        PREVIEW_MODEL_TYPES.register(location, renderer);
    }
    
    public static void registerItemColor(ItemColors colors, Item item) {
        colors.register(ITEM_COLOR, item);
    }
    
    public static float getFrameTime() {
        if (mc.isPaused())
            return 1.0F;
        return mc.getTimer().getGameTimeDeltaPartialTick(false);
    }
    
    @SubscribeEvent
    public static void commands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("cmdclientconfig").executes((x) -> {
            try {
                GuiEventHandler.queueScreen(new GuiScreenIntegration(new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Side.CLIENT)));
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return 1;
        }));
    }
    
    public static void init(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(CreativeCoreClient.class);
        NeoForge.EVENT_BUS.register(GuiEventHandler.class);
        GuiStyle.reload();
        Minecraft minecraft = Minecraft.getInstance();
        ReloadableResourceManager reloadableResourceManager = (ReloadableResourceManager) minecraft.getResourceManager();
        
        minecraft.execute(() -> {
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
        });
    }
    
    public static void modelEvent(RegisterGeometryLoaders event) {
        event.register(ResourceLocation.tryBuild(CreativeCore.MODID, "layer"), new CreativeModelLayerLoader());
        event.register(ResourceLocation.tryBuild(CreativeCore.MODID, "empty"), new CreativeModelEmptyLoader());
    }
    
    public static void screenEvent(RegisterMenuScreensEvent event) {
        event.register(CreativeCore.GUI_CONTAINER, new ScreenConstructor<ContainerIntegration, ContainerScreenIntegration>() {
            
            @Override
            public ContainerScreenIntegration create(ContainerIntegration container, Inventory inventory, Component p_create_3_) {
                return new ContainerScreenIntegration(container, inventory);
            }
        });
    }
    
    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof IScaleableGuiScreen gui)
            gui.clientTick();
    }
}
