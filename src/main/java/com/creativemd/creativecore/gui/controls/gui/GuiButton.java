package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.gui.GuiRenderHelper;

public abstract class GuiButton extends GuiClickableLabel{
	
	public GuiButton(String caption, int x, int y)
	{
		this(caption, x, y, GuiRenderHelper.instance.getStringWidth(caption));
	}
	
	public GuiButton(String caption, int x, int y, int width)
	{
		this(caption, caption, x, y, width, 14);
	}
	
	public GuiButton(String name, String caption, int x, int y, int width)
	{
		this(name, caption, x, y, width, 14);
	}
	
	public GuiButton(String name, String caption, int x, int y, int width, int height) {
		super(name, caption, x, y, width, height, ColorUtils.WHITE);
	}
	
	public GuiButton(String caption, int x, int y, int width, int height) {
		this(caption, caption, x, y, width, height);
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
