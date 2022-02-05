package team.creative.creativecore.common.gui.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.handler.GuiLayerHandler;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;

public class LayerOpenPacket extends LayerPacket {
    
    public String handler;
    public CompoundTag nbt;
    
    public LayerOpenPacket(String handler, CompoundTag nbt) {
        this.handler = handler;
        this.nbt = nbt;
    }
    
    public LayerOpenPacket() {}
    
    @Override
    public void execute(Player player, IGuiIntegratedParent container) {
        container.openLayer(GuiLayerHandler.create(container, handler, nbt));
    }
    
}
