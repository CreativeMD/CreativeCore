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
		int size = 16+4+renderer.getStringWidth(caption);
		GL11.glPushMatrix();
		GL11.glTranslated(width/2, 0, 0);
		GL11.glTranslated(-size/2, height/2-8, 0);
		this.avatar.handleRendering(mc, renderer, 16, 16);
		
		GL11.glTranslated(16-(size/2)+renderer.getStringWidth(caption)/2, -1, 0);
		
		int l = 14737632;
		
		if(isMouseOver())
		{
			l = 16777120;
		}
		
		if (!this.enabled)
        {
            l = 10526880;
        }
		renderer.drawStringWithShadow(caption, width / 2 - renderer.getStringWidth(caption) / 2, (this.height - 8) / 2, l);
		GL11.glPopMatrix();
		
	}
	
	@Override
	public ArrayList<String> getTooltip()
	{
		ArrayList<String> tooltip = new ArrayList<String>();
		tooltip.add(caption);
		return tooltip;
	}
}
