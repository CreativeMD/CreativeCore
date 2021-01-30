package team.creative.creativecore.common.gui.integration;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;

public class ContainerIntegration extends Container implements IGuiParent {
	
	private List<GuiLayer> layers = new ArrayList<>();
	private final PlayerEntity player;
	
	protected ContainerIntegration(ContainerType<?> type, int id, PlayerEntity player, GuiLayer layer) {
		super(type, id);
		layer.setParent(this);
		this.layers.add(layer);
		this.player = player;
	}
	
	public void tick() {
		for (GuiLayer layer : layers)
			layer.tick();
	}
	
	public List<GuiLayer> getLayers() {
		return layers;
	}
	
	public GuiLayer getTopLayer() {
		return layers.get(layers.size() - 1);
	}
	
	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		for (GuiLayer layer : layers)
			layer.closed();
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
	
	@Override
	public PlayerEntity getPlayer() {
		return player;
	}
	
	@Override
	public boolean isClient() {
		return player.world.isRemote;
	}
	
	@Override
	public void moveBehind(GuiControl toMove, GuiControl reference) {
		layers.remove(toMove);
		int index = layers.indexOf(reference);
		if (index != -1 && index < layers.size() - 1)
			layers.add(index + 1, (GuiLayer) toMove);
		else
			moveBottom(toMove);
	}
	
	@Override
	public void moveInFront(GuiControl toMove, GuiControl reference) {
		layers.remove(toMove);
		int index = layers.indexOf(reference);
		if (index != -1)
			layers.add(index, (GuiLayer) toMove);
		else
			moveTop(toMove);
	}
	
	@Override
	public void moveTop(GuiControl toMove) {
		layers.remove(toMove);
		layers.add(0, (GuiLayer) toMove);
	}
	
	@Override
	public void moveBottom(GuiControl toMove) {
		layers.remove(toMove);
		layers.add((GuiLayer) toMove);
	}
	
}
