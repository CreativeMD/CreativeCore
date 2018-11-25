package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

public class GuiTextBox extends GuiControl {
	
	public String text;
	public int color;
	
	public GuiTextBox(String name, String text, int x, int y, int width, int color) {
		super(name, x, y, width, font.FONT_HEIGHT);
		setStyle(Style.liteStyleNoHighlight);
		setText(text);
		this.color = color;
	}
	
	public GuiTextBox(String name, String text, int x, int y, int width) {
		this(name, text, x, y, width, ColorUtils.WHITE);
	}
	
	public void setText(String text) {
		while (text != null && text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}
		this.text = text.replace("\\n", "" + '\n');
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		int y = 0;
		for (String s : font.listFormattedStringToWidth(text, width)) {
			font.drawString(s, 0, y, color, true);
			y += font.FONT_HEIGHT;
		}
		this.height = y + getContentOffset() * 2;
	}
	
}
