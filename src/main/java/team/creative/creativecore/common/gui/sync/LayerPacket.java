package team.creative.creativecore.common.gui.sync;

import net.minecraft.entity.player.PlayerEntity;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.network.CreativePacket;

public abstract class LayerPacket extends CreativePacket {
    
    public int layer;
    
    public LayerPacket(int layer) {
        this.layer = layer;
    }
    
    public LayerPacket() {
        
    }
    
    @Override
    public void executeClient(PlayerEntity player) {
        if (player.openContainer instanceof IGuiIntegratedParent)
            execute(player, (IGuiIntegratedParent) player.openContainer);
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        if (player.openContainer instanceof IGuiIntegratedParent)
            execute(player, (IGuiIntegratedParent) player.openContainer);
    }
    
    public abstract void execute(PlayerEntity player, IGuiIntegratedParent container);
    
}
