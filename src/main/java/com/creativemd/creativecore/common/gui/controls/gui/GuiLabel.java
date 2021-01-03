package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

public class GuiLabel extends GuiControl {
	
	protected String caption;
	public int color;
	
	public GuiLabel(String caption, int x, int y) {
		this(caption, x, y, ColorUtils.WHITE);
	}
	
	public GuiLabel(String name, String caption, int x, int y) {
		this(name, caption, x, y, ColorUtils.WHITE);
	}
	
	public GuiLabel(String name, String caption, int x, int y, int color) {
		this(name, caption, x, y, GuiRenderHelper.instance.getStringWidth(caption), GuiRenderHelper.instance.getFontHeight(), color);
	}
	
	public GuiLabel(String caption, int x, int y, int color) {
		this(caption, caption, x, y, color);
	}
	
	public GuiLabel(String caption, int x, int y, int width, int height, int color) {
		this(caption, caption, x, y, width, height, color);
	}
	
	public GuiLabel(String name, String caption, int x, int y, int width, int height, int color) {
		super(name, x, y, width, height);
		this.width += getAdditionalSize();
		this.color = color;
		this.caption = caption;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
		this.width = font.getStringWidth(caption) + getContentOffset() * 2;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		if (shouldDrawTitle())
			helper.drawStringWithShadow(caption, 0, 0, width, height, getColor(), getAdditionalSize());
	}
	
	protected int getAdditionalSize() {
		return 0;
	}
	
	@Override
	public boolean hasBorder() {
		return false;
	}
	
	@Override
	public boolean hasBackground() {
		return false;
	}
	
	public boolean shouldDrawTitle() {
		return true;
	}
	
	public int getColor() {
		return color;
	}
	
}
