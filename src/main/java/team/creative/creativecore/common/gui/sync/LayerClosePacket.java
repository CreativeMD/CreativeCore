package team.creative.creativecore.common.gui.sync;

import net.minecraft.entity.player.PlayerEntity;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;

public class LayerClosePacket extends LayerPacket {
    
    public LayerClosePacket(int layer) {
        super(layer);
    }
    
    public LayerClosePacket() {
        
    }
    
    @Override
    public void execute(PlayerEntity player, IGuiIntegratedParent container) {
        container.closeLayer(layer);
    }
    
}
