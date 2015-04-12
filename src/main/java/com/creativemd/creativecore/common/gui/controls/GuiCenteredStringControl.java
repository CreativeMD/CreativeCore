package com.creativemd.creativecore.common.gui.controls;

import net.minecraft.client.gui.FontRenderer;

public class GuiCenteredStringControl extends GuiStringControl{

	public GuiCenteredStringControl(FontRenderer font, String title, int x, int y) {
		super(font, title, x, y);
		this.posX += width/2;
		this.posY += height/2;
	}

}
