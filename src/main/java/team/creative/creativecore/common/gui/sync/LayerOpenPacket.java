package team.creative.creativecore.common.gui.sync;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import team.creative.creativecore.common.gui.handler.GuiLayerHandler;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;

public class LayerOpenPacket extends LayerPacket {
    
    public String handler;
    public CompoundNBT nbt;
    
    public LayerOpenPacket(String handler, CompoundNBT nbt) {
        this.handler = handler;
        this.nbt = nbt;
    }
    
    public LayerOpenPacket() {
        
    }
    
    @Override
    public void execute(PlayerEntity player, IGuiIntegratedParent container) {
        container.openLayer(GuiLayerHandler.create(container, handler, nbt));
    }
    
}
