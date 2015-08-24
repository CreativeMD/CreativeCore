package com.creativemd.creativecore.common.gui.controls;

import net.minecraft.client.gui.FontRenderer;

public class GuiLabel extends GuiControl{
	
	public String title;
	public int color;
	
	public GuiLabel(String title, int x, int y, int color)
	{
		this(mc.fontRenderer, title, x, y, color);
		
	}
	
	public GuiLabel(String title, int x, int y)
	{
		this(title, x, y, 14737632);
	}
	
	public GuiLabel(FontRenderer font, String title, int x, int y, int color)
	{
		super(title, x, y, font.getStringWidth(title), font.FONT_HEIGHT);
		this.title = title;
		this.color = color;
	}
	
	@Override
	public void drawControl(FontRenderer renderer) {
		renderer.drawStringWithShadow(title, 0, height/4, getColor());
	}
	
	public int getColor()
	{
		return color;
	}

}
