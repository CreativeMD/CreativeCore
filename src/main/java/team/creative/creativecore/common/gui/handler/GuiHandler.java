package team.creative.creativecore.common.gui.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.sync.OpenGuiPacket;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

@FunctionalInterface
public interface GuiHandler {
    
    public static final NamedHandlerRegistry<GuiHandler> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static void register(String name, GuiHandler handler) {
        REGISTRY.register(name, handler);
    }
    
    public static void openGui(String name, CompoundTag nbt, Player player) {
        GuiHandler handler = REGISTRY.get(name);
        if (handler != null) {
            if (player.level.isClientSide)
                CreativeCore.NETWORK.sendToServer(new OpenGuiPacket(name, nbt));
            else
                player.openMenu(new SimpleMenuProvider((id, inventory, x) -> {
                    ContainerIntegration integration = new ContainerIntegration(CreativeCore.GUI_CONTAINER, id, x, handler.create(player, nbt));
                    return integration;
                }, new TextComponent(name)));
        }
    }
    
    public GuiLayer create(Player player, CompoundTag nbt);
    
}
