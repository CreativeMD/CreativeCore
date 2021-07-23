package team.creative.creativecore.common.gui.handler;

import java.util.HashMap;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.ScreenManager;

import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.integration.ContainerScreenIntegration;
import team.creative.creativecore.common.gui.sync.OpenGuiPacket;
import team.creative.creativecore.common.network.CreativePacket;

public class GuiContainerHandler {
    
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, CreativeCore.MODID);
    
    private static HashMap<String, GuiContainerHandler> guihandlers = new HashMap<String, GuiContainerHandler>();
    
    MenuProvider provider;
    Supplier<MenuType<ContainerIntegration>> type;
    
    public GuiContainerHandler() {}
    
    public static <T extends CreativePacket> void registerGuiHandler(String handlerid, GuiHandlerPlayer handler) {
        guihandlers.put(handlerid, handler);
        handler.type = CONTAINERS.register(handlerid, () -> new MenuType<ContainerIntegration>(null) {
            @Override
            public ContainerIntegration create(int windowId, Inventory playerInv, net.minecraft.network.FriendlyByteBuf extraData) {
                return new ContainerIntegration(this, windowId, playerInv.player, handler.create(playerInv.player));
            }
            
            @Override
            public ContainerIntegration create(int windowId, Inventory playerInv) {
                return new ContainerIntegration(this, windowId, playerInv.player, handler.create(playerInv.player));
            }
        });
        
        handler.provider = new SimpleMenuProvider((id, inventory, player) -> {
            ContainerIntegration integration = new ContainerIntegration(handler.type.get(), id, player, handler.create(player));
            return integration;
        }, new TextComponent(handlerid));
    }
    
    public static GuiContainerHandler getHandler(String id) {
        return guihandlers.get(id);
    }
    
    public static void openGui(Player player, String name) {
        GuiContainerHandler handler = getHandler(name);
        if (handler != null) {
            if (player.level.isClientSide)
                CreativeCore.NETWORK.sendToServer(new OpenGuiPacket(name));
            else
                player.openMenu(handler.provider);
        }
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public static void initClient() {
        for (GuiContainerHandler handler : guihandlers.values())
            ScreenManager.register(handler.type.get(), new IScreenFactory<ContainerIntegration, ContainerScreenIntegration>() {
                
                @Override
                public ContainerScreenIntegration create(ContainerIntegration container, Inventory inventory, Component p_create_3_) {
                    return new ContainerScreenIntegration(container, inventory);
                }
            });
        //ScreenManager.registerFactory(handler.type.get(), (container, inventory, text) -> new ContainerScreenIntegration(container, inventory));
    }
    
    public static abstract class GuiHandlerPlayer extends GuiContainerHandler {
        
        public abstract GuiLayer create(Player player);
        
    }
    
}
