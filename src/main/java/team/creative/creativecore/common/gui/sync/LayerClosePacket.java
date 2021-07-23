package team.creative.creativecore.common.gui.sync;

import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;

public class LayerClosePacket extends LayerPacket {
    
    public LayerClosePacket() {
        
    }
    
    @Override
    public void execute(Player player, IGuiIntegratedParent container) {
        container.closeLayer(container.getLayers().size() - 1);
    }
    
}
