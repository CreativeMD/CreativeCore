package team.creative.creativecore.common.gui.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.creator.GuiCreator;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.network.CreativePacket;

public class OpenGuiPacket extends CreativePacket {
    
    public String name;
    public CompoundTag nbt;
    
    public OpenGuiPacket() {}
    
    public OpenGuiPacket(String name, CompoundTag nbt) {
        this.name = name;
        this.nbt = nbt;
    }
    
    @Override
    public void executeClient(Player player) {
        if (player.containerMenu instanceof IGuiIntegratedParent gui)
            gui.openLayer(GuiCreator.REGISTRY.get(name).function.apply(nbt, player));
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        openGuiOnServer(GuiCreator.REGISTRY.get(name), nbt, player);
    }
    
    public static void openGuiOnServer(GuiCreator creator, CompoundTag nbt, ServerPlayer player) {
        var layer = creator.function.apply(nbt, player);
        player.openMenu(new SimpleMenuProvider((id, inventory, x) -> new ContainerIntegration(CreativeCore.GUI_CONTAINER, id, x, layer), Component.literal(creator.getName())));
        CreativeCore.NETWORK.sendToClient(new OpenGuiPacket(creator.getName(), nbt), player);
        layer.init();
    }
    
}
