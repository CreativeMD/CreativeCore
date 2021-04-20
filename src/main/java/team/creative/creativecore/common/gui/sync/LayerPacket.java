package team.creative.creativecore.common.gui.sync;

import net.minecraft.entity.player.PlayerEntity;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.network.CreativePacket;

public abstract class LayerPacket extends CreativePacket {
    
    public LayerPacket() {
        
    }
    
    @Override
    public void executeClient(PlayerEntity player) {
        if (player.containerMenu instanceof IGuiIntegratedParent)
            execute(player, (IGuiIntegratedParent) player.containerMenu);
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        if (player.containerMenu instanceof IGuiIntegratedParent)
            execute(player, (IGuiIntegratedParent) player.containerMenu);
    }
    
    public abstract void execute(PlayerEntity player, IGuiIntegratedParent container);
    
}
