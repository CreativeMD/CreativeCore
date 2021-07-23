package team.creative.creativecore.common.gui.sync;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.handler.GuiContainerHandler;
import team.creative.creativecore.common.network.CreativePacket;

public class OpenGuiPacket extends CreativePacket {
    
    public String name;
    
    public OpenGuiPacket() {
        
    }
    
    public OpenGuiPacket(String name) {
        this.name = name;
    }
    
    @Override
    public void executeClient(Player player) {
        
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        GuiContainerHandler.openGui(player, name);
    }
    
}
