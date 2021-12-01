package team.creative.creativecore.common.gui.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.handler.GuiHandler;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.network.CreativePacket;

public class OpenGuiPacket extends CreativePacket {
    
    public String name;
    public CompoundTag nbt;
    
    public OpenGuiPacket() {
        
    }
    
    public OpenGuiPacket(String name, CompoundTag nbt) {
        this.name = name;
        this.nbt = nbt;
    }
    
    @Override
    public void executeClient(Player player) {
        if (player.containerMenu instanceof IGuiIntegratedParent gui)
            gui.openLayer(GuiHandler.REGISTRY.get(name).create(player, nbt));
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        GuiHandler.openGui(name, nbt, player);
    }
    
}
