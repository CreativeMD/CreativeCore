package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector4d;

import net.minecraft.client.gui.FontRenderer;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.rendering.RenderHelper2D;

public class GuiAvatarLabel extends GuiLabelClickable{
	
	public Avatar avatar;
	
	public GuiAvatarLabel(String title, int x, int y, int color, Avatar avatar) {
		super(title, x, y, color);
		this.avatar = avatar;
	}

	@Override
	public boolean shouldDrawTitle()
	{
		return false;
	}
	
	@Override
	public void drawControl(FontRenderer renderer) {
		super.drawControl(renderer);
		avatar.handleRendering(mc, renderer, 18, 18);
		renderer.drawStringWithShadow(title, 20, height/4, getColor());
	}
}
