package team.creative.creativecore.common.gui.sync;

import net.minecraft.entity.player.PlayerEntity;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;

public class LayerClosePacket extends LayerPacket {
    
    public LayerClosePacket() {
        
    }
    
    @Override
    public void execute(PlayerEntity player, IGuiIntegratedParent container) {
        container.closeTopLayer();
    }
    
}
