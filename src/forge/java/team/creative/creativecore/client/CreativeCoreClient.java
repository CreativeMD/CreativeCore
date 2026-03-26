package team.creative.creativecore.client;

import static team.creative.creativecore.CreativeCore.LOGGER;

import java.util.List;
import java.util.Optional;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.client.gui.GuiScreenHandler;
import team.creative.creativecore.client.gui.integration.ContainerScreenIntegration;
import team.creative.creativecore.client.gui.integration.GuiScreenIntegration;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.style.GuiStyle;

public class CreativeCoreClient {
    
    public static void load(IEventBus bus) {
        bus.addListener(CreativeCoreClient::init);
        bus.addListener(CreativeCoreClient::screenEvent);
        bus.addListener(CreativeCoreClient::reloadListener);
        
        // TODO READD QuadLighterMixin, CreativeUnbakedModel, CreativeQuadLighter, CreativePlatformHooks, CreativeModelLoader, CreativeItemModel, CreativeItemBoxModel, CreativeBlockModel
        // TODO READD CreativeBakedQuad, CreativeBakedModel, CreativeBakedBoxModelTranslucent, CreativeBakedBoxModel
        
    }
    
    public static void registerClientConfig(String modid) {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ((a, b) -> {
            ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(modid);
            if (holder != null && !holder.isEmpty(Side.CLIENT))
                return new GuiScreenIntegration(new ConfigGuiLayer(true, holder, Side.CLIENT));
            return null;
        }));
    }
    
    public static float getFrameTime() {
        var mc = Minecraft.getInstance();
        if (mc.isPaused())
            return 1.0F;
        return mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }
    
    @SubscribeEvent
    public static void commands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("cmdclientconfig").executes((x) -> {
            try {
                GuiScreenHandler.queueScreen(new GuiScreenIntegration(new ConfigGuiLayer(true, CreativeConfigRegistry.ROOT, Side.CLIENT)));
            } catch (Exception e) {
                LOGGER.error(e);
                e.printStackTrace();
            }
            return 1;
        }));
    }
    
    public static void init(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(CreativeCoreClient.class);
        NeoForge.EVENT_BUS.register(GuiScreenHandler.class);
        GuiStyle.reload();
    }
    
    public static void reloadListener(AddClientReloadListenersEvent event) {
        event.addListener(Identifier.tryBuild(CreativeCore.MODID, "gui"), new SimplePreparableReloadListener() {
            
            @Override
            protected Object prepare(ResourceManager p_10796_, ProfilerFiller p_10797_) {
                return GuiStyle.class; // No idea
            }
            
            @Override
            protected void apply(Object p_10793_, ResourceManager p_10794_, ProfilerFiller p_10795_) {
                GuiStyle.reload();
            }
        });
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
    
    public static void postBackgroundEvent(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        NeoForge.EVENT_BUS.post(new ScreenEvent.Render.Background(screen, graphics, mouseX, mouseY, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false)));
    }
    
    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent,
            int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        return ClientHooks.gatherTooltipComponents(stack, textElements, itemComponent, mouseX, screenWidth, screenHeight, fallbackFont);
    }
    
}
