package team.creative.creativecore.common.gui.sync;

import net.minecraft.entity.player.PlayerEntity;
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
    public void executeClient(PlayerEntity player) {
        
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        GuiContainerHandler.openGui(player, name);
    }
    
}
