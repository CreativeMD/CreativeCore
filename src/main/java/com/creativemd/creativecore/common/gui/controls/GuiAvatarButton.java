package com.creativemd.creativecore.common.gui.controls;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.client.rendering.RenderHelper3D;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;

public class GuiAvatarButton extends GuiButtonControl{
	
	public Avatar avatar;
	
	public GuiAvatarButton(String caption, int x, int y, int width, int height, int id, Avatar avatar) {
		super(caption, x, y, width, height, id);
		this.avatar = avatar;
	}
	
	@Override
	public boolean shouldDrawCaption()
	{
		return false;
	}
	
	@Override
	public void drawControl(FontRenderer renderer) {
		super.drawControl(renderer);
		this.avatar.handleRendering(mc, renderer, width, height);
	}
	
	@Override
	public ArrayList<String> getTooltip()
	{
		ArrayList<String> tooltip = new ArrayList<String>();
		tooltip.add(caption);
		return tooltip;
	}
}
