package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;

public class GuiLabel extends GuiControl {
	
	public String caption;
	public int color;
	
	public GuiLabel(String caption, int x, int y) {
		this(caption, x, y, 14737632);
	}
	
	public GuiLabel(String name, String caption, int x, int y) {
		this(name, caption, x, y, 14737632);
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
