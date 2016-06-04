package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.gui.GuiRenderHelper;

public abstract class GuiButton extends GuiClickableLabel{
	
	public GuiButton(String caption, int x, int y)
	{
		this(caption, x, y, GuiRenderHelper.instance.getStringWidth(caption));
	}
	
	public GuiButton(String caption, int x, int y, int width)
	{
		this(caption, x, y, width, 14);
	}
	
	public GuiButton(String caption, int x, int y, int width, int height) {
		super(caption, x, y, width, height, ColorUtils.WHITE);
	}
	
	@Override
	public boolean hasBorder()
	{
		return true;
	}
	
	@Override
	public boolean hasBackground()
	{
		return true;
	}
	
}
