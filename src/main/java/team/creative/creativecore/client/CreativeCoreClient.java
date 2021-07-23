package team.creative.creativecore.client;

import java.util.function.Predicate;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import team.creative.creativecore.client.command.ClientCommandRegistry;
import team.creative.creativecore.client.test.GuiTest;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.gui.handler.GuiContainerHandler;
import team.creative.creativecore.common.gui.integration.GuiEventHandler;
import team.creative.creativecore.common.gui.integration.GuiScreenIntegration;
import team.creative.creativecore.common.gui.style.GuiStyle;

public class CreativeCoreClient {
    
    private static Minecraft mc = Minecraft.getInstance();
    
    public static void registerClientConfig(String modid) {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.CONFIGGUIFACTORY, () -> (a, b) -> {
            ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(modid);
            if (holder != null && !holder.isEmpty(Dist.CLIENT))
                return new GuiScreenIntegration(new ConfigGuiLayer(holder, Dist.CLIENT));
            return null;
        });
    }
    
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientCommandRegistry.register((LiteralArgumentBuilder<ISuggestionProvider>) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("test-client")).executes((x) -> {
                mc.player.createCommandSourceStack().sendSuccess(new StringTextComponent("Successful!"), false);
                return 1;
            }));
            
            ClientCommandRegistry.register((LiteralArgumentBuilder<ISuggestionProvider>) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("test-gui")).executes((x) -> {
                try {
                    GuiEventHandler.queueScreen(new GuiScreenIntegration(new GuiTest(200, 200)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            }));
            
            ClientCommandRegistry
                    .register((LiteralArgumentBuilder<ISuggestionProvider>) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("cmdclientconfig")).executes((x) -> {
                        try {
                            GuiEventHandler.queueScreen(new GuiScreenIntegration(new ConfigGuiLayer(CreativeConfigRegistry.ROOT, Dist.CLIENT)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 1;
                    }));
        });
        
        GuiStyle.reload();
        Minecraft minecraft = Minecraft.getInstance();
        IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager) minecraft.getResourceManager();
        
        reloadableResourceManager.registerReloadListener(new ISelectiveResourceReloadListener() {
            
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
                GuiStyle.reload();
            }
        });
        GuiContainerHandler.initClient();
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void chat(ClientChatEvent event) {
        String message = event.getMessage();
        if (message.startsWith("/") && ClientCommandRegistry.handleCommand(mc.player.createCommandSourceStack(), message) != -1) {
            event.setCanceled(true);
            mc.gui.getChat().addRecentChat(message);
        }
    }
}
