package team.creative.creativecore.common.gui.handler;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.StringTextComponent;
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
    
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, CreativeCore.MODID);
    
    private static HashMap<String, GuiContainerHandler> guihandlers = new HashMap<String, GuiContainerHandler>();
    
    INamedContainerProvider provider;
    Supplier<ContainerType<ContainerIntegration>> type;
    
    public GuiContainerHandler() {}
    
    public static <T extends CreativePacket> void registerGuiHandler(String handlerid, GuiHandlerPlayer handler) {
        guihandlers.put(handlerid, handler);
        handler.type = CONTAINERS.register(handlerid, () -> new ContainerType<ContainerIntegration>(null) {
            @Override
            public ContainerIntegration create(int windowId, PlayerInventory playerInv, net.minecraft.network.PacketBuffer extraData) {
                return new ContainerIntegration(this, windowId, playerInv.player, handler.create(playerInv.player));
            }
            
            @Override
            public ContainerIntegration create(int windowId, PlayerInventory playerInv) {
                return new ContainerIntegration(this, windowId, playerInv.player, handler.create(playerInv.player));
            }
        });
        
        handler.provider = new SimpleNamedContainerProvider((id, inventory, player) -> {
            ContainerIntegration integration = new ContainerIntegration(handler.type.get(), id, player, handler.create(player));
            return integration;
        }, new StringTextComponent(handlerid));
    }
    
    public static GuiContainerHandler getHandler(String id) {
        return guihandlers.get(id);
    }
    
    public static void openGui(PlayerEntity player, String name) {
        GuiContainerHandler handler = getHandler(name);
        if (handler != null) {
            if (player.world.isRemote)
                CreativeCore.NETWORK.sendToServer(new OpenGuiPacket(name));
            else
                player.openContainer(handler.provider);
        }
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public static void initClient() {
        for (GuiContainerHandler handler : guihandlers.values())
            ScreenManager.registerFactory(handler.type.get(), (container, inventory, text) -> new ContainerScreenIntegration(container, inventory));
    }
    
    public static abstract class GuiHandlerPlayer extends GuiContainerHandler {
        
        public abstract GuiLayer create(PlayerEntity player);
        
    }
    
}
