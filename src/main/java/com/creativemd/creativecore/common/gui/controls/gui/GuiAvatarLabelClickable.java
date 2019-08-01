package com.creativemd.creativecore.common.gui.controls.gui;

import java.util.List;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.controls.container.client.GuiSlotControl;

import net.minecraft.client.renderer.GlStateManager;

public abstract class GuiAvatarLabelClickable extends GuiClickableLabel {
	
	public int avatarSize = 16;
	public int spaceBetween = 6;
	public Avatar avatar;
	
	public boolean avatarLeft;
	
	public GuiAvatarLabelClickable(String name, String title, int x, int y, int color, Avatar avatar, boolean avatarLeft) {
		super(name, title, x, y, GuiRenderHelper.instance.getStringWidth(title), 16, color);
		this.width += getAdditionalSize();
		this.avatar = avatar;
		this.avatarLeft = avatarLeft;
	}
	
	public GuiAvatarLabelClickable(String title, int x, int y, int color, Avatar avatar) {
		this(title, title, x, y, color, avatar, true);
	}
	
	@Override
	protected int getAdditionalSize() {
		return avatarSize + spaceBetween;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		if (avatarLeft) {
			super.renderContent(helper, style, width, height);
			GlStateManager.pushMatrix();
			GlStateManager.translate(width / 2 - (helper.getStringWidth(caption) + getAdditionalSize()) / 2, height / 2 - avatarSize / 2, 0);
			avatar.handleRendering(helper, avatarSize, avatarSize);
			GlStateManager.popMatrix();
		} else {
			helper.font.drawStringWithShadow(caption, 0, 0, getColor());
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(helper.font.getStringWidth(caption) + spaceBetween, height / 2 - avatarSize / 2, 0);
			avatar.handleRendering(helper, avatarSize, avatarSize);
			GlStateManager.popMatrix();
		}
	}
	
	@Override
	public List<String> getTooltip() {
		if (avatar instanceof AvatarItemStack)
			return GuiSlotControl.getTooltip(((AvatarItemStack) avatar).stack);
		return super.getTooltip();
	}
}
