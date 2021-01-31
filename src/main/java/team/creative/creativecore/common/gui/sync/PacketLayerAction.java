package team.creative.creativecore.common.gui.sync;

import net.minecraft.entity.player.PlayerEntity;
import team.creative.creativecore.common.gui.integration.ContainerIntegration;
import team.creative.creativecore.common.network.CreativePacket;

public class PacketLayerAction extends CreativePacket {
	
	public LayerAction action;
	public int layer;
	
	public PacketLayerAction(LayerAction action, int layer) {
		this.action = action;
		this.layer = layer;
	}
	
	public PacketLayerAction() {
		
	}
	
	@Override
	public void executeClient(PlayerEntity player) {
		if (player.openContainer instanceof ContainerIntegration)
			action.perform((ContainerIntegration) player.openContainer, layer);
	}
	
	@Override
	public void executeServer(PlayerEntity player) {
		if (player.openContainer instanceof ContainerIntegration)
			action.perform((ContainerIntegration) player.openContainer, layer);
	}
	
	public static enum LayerAction {
		
		CLOSE {
			@Override
			public void perform(ContainerIntegration container, int layer) {
				container.closeLayer(layer);
			}
		};
		
		public abstract void perform(ContainerIntegration container, int layer);
		
	}
	
}
