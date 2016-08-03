package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

public abstract class GuiAvatarLabelClickable extends GuiClickableLabel{
	
	public int avatarSize = 16;
	public int spaceBetween = 6;
	public Avatar avatar;
	
	public GuiAvatarLabelClickable(String name, String title, int x, int y, int color, Avatar avatar) {
		super(name, title, x, y, GuiRenderHelper.instance.getStringWidth(title), 16, color);
		this.width += getAdditionalSize();
		this.avatar = avatar;
	}
	
	public GuiAvatarLabelClickable(String title, int x, int y, int color, Avatar avatar) {
		this(title, title, x, y, color, avatar);
	}
	
	@Override
	protected int getAdditionalSize()
	{
		return avatarSize+spaceBetween;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		super.renderContent(helper, style, width, height);
		GlStateManager.pushMatrix();
		GlStateManager.translate(width/2-(helper.getStringWidth(caption)+getAdditionalSize())/2, height/2-avatarSize/2, 0);
		avatar.handleRendering(helper, avatarSize, avatarSize);
		GlStateManager.popMatrix();
	}
}
