package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.packet.GuiPacket;
import com.creativemd.creativecore.common.packet.PacketHandler;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SubGui {
	
	public String title;
	
	public abstract ArrayList<GuiControl> getControls();
	
	public abstract void drawForeground(FontRenderer fontRenderer);
	
	public abstract void drawBackground(FontRenderer fontRenderer);
	
	public void onControlClicked(GuiControl control){}
	
	public void onMouseDragged(GuiControl control){}
	
	public void onMouseReleased(GuiControl control){}
	
	public void sendGuiPacket(int control, String value)
	{
		PacketHandler.sendPacketToServer(new GuiPacket(control, value));
	}
}
