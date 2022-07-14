package team.creative.creativecore.common.gui.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.creator.GuiLayerCreator;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;

public class LayerOpenPacket extends LayerPacket {
    
    public String handler;
    public CompoundTag nbt;
    
    public LayerOpenPacket(GuiLayerCreator handler, CompoundTag nbt) {
        this.handler = GuiLayerCreator.REGISTRY.getId(handler);
        this.nbt = nbt;
    }
    
    public LayerOpenPacket() {}
    
    @Override
    public void execute(Player player, IGuiIntegratedParent container) {
        container.openLayer(GuiLayerCreator.create(container, handler, nbt));
    }
    
}
