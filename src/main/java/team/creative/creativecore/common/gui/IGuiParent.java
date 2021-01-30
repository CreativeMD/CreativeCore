package team.creative.creativecore.common.gui;

import net.minecraft.entity.player.PlayerEntity;

public interface IGuiParent {
	
	public boolean isClient();
	
	public PlayerEntity getPlayer();
	
	public void moveBehind(GuiControl toMove, GuiControl reference);
	
	public void moveInFront(GuiControl toMove, GuiControl reference);
	
	public void moveTop(GuiControl toMove);
	
	public void moveBottom(GuiControl toMove);
	
}
