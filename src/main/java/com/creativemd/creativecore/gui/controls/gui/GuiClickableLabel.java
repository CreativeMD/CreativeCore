package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.gui.GuiRenderHelper;

public abstract class GuiClickableLabel extends GuiLabel{

	public GuiClickableLabel(String caption, int x, int y)
	{
		super(caption, x, y, 14737632);
	}
	
	public GuiClickableLabel(String caption, int x, int y, int color)
	{
		super(caption, x, y, GuiRenderHelper.instance.getStringWidth(caption), GuiRenderHelper.instance.getFontHeight(), color);
	}
	
	public GuiClickableLabel(String caption, int x, int y, int width, int height, int color)
	{
		super(caption, x, y, width, height, color);
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button)
	{
		playSound(buttonClicked);
		onClicked(x, y, button);
		return true;
	}
	
	public abstract void onClicked(int x, int y, int button);
}
