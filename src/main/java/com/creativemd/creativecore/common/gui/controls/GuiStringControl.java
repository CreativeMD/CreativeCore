package com.creativemd.creativecore.common.gui.controls;

import net.minecraft.client.gui.FontRenderer;

public class GuiStringControl extends GuiControl{
	
	public String title;
	
	public GuiStringControl(FontRenderer font, String title, int x, int y)
	{
		super(x, y, font.getStringWidth(title), font.FONT_HEIGHT);
		this.title = title;
	}
	
	@Override
	public void drawControl(FontRenderer renderer) {
		renderer.drawString(title, posX, posY, 0);
	}

}
