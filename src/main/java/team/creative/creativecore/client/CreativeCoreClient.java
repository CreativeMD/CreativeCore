package team.creative.creativecore.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.client.render.model.CreativeRenderBlock;
import team.creative.creativecore.client.render.model.CreativeRenderItem;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.integration.ContainerScreenIntegration;
import team.creative.creativecore.common.gui.integration.GuiEventHandler;
import team.creative.creativecore.common.gui.integration.GuiScreenIntegration;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.registry.FilteredHandlerRegistry;

public class CreativeCoreClient {
    
    private static Minecraft mc = Minecraft.getInstance();
    
    public static final FilteredHandlerRegistry<Item, CreativeRenderItem> RENDERED_ITEMS = new FilteredHandlerRegistry<>(null);
    public static final FilteredHandlerRegistry<Block, CreativeRenderBlock> RENDERED_BLOCKS = new FilteredHandlerRegistry<>(null);
    
    private static final ItemColor ITEM_COLOR = (stack, tint) -> tint;
    
    public static void registerClientConfig(String modid) {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiFactory.class, () -> new ConfigGuiFactory((a, b) -> {
            ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(modid);
            if (holder != null && !holder.isEmpty(Dist.CLIENT))
                return new GuiScreenIntegration(new ConfigGuiLayer(holder, Dist.CLIENT));
            return null;
        }));
    }
    
    public static void registerBlocks(CreativeRenderBlock renderer, Block... blocks) {
        for (int i = 0; i < blocks.length; i++)
            RENDERED_BLOCKS.register(blocks[i], renderer);
    }
    
    public static void registerBlock(CreativeRenderBlock renderer, Block block) {
        RENDERED_BLOCKS.register(block, renderer);
    }
    
    public static void registerItem(CreativeRenderItem renderer, Item item) {
        RENDERED_ITEMS.register(item, renderer);
        mc.getItemColors().register(ITEM_COLOR, item);
    }
    
    public static float getDeltaFrameTime() {
        if (mc.isPaused())
            return 1.0F;
        return mc.getDeltaFrameTime();
    }
    
    public static void commands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder<CommandSourceStack>) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("cmdclientconfig")).executes((x) -> {
            try {
                GuiEventHandler.queueScreen(new GuiScreenIntegration(new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Dist.CLIENT)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 1;
        }));
    }
    
    public static void init(FMLClientSetupEvent event) {
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
                for (CreativeRenderItem handler : RENDERED_ITEMS.handlers())
                    handler.reload();
            }
        });
        
        MenuScreens.register(CreativeCore.GUI_CONTAINER, new ScreenConstructor<ContainerIntegration, ContainerScreenIntegration>() {
            
            @Override
            public ContainerScreenIntegration create(ContainerIntegration container, Inventory inventory, Component p_create_3_) {
                return new ContainerScreenIntegration(container, inventory);
            }
        });
    }
    
    @SubscribeEvent
    public static void clientTick(ClientTickEvent event) {
        if (event.phase == Phase.START && Minecraft.getInstance().screen instanceof IScaleableGuiScreen)
            ((IScaleableGuiScreen) Minecraft.getInstance().screen).clientTick();
    }
}
