package team.creative.creativecore.common.gui.packet;

import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;

public class LayerClosePacket extends LayerPacket {
    
    public int index;
    
    public LayerClosePacket(int index) {
        this.index = index;
    }
    
    public LayerClosePacket() {}
    
    @Override
    public void execute(Player player, IGuiIntegratedParent container) {
        if (container.getLayers().size() > index)
            container.closeLayer(index);
    }
    
}
