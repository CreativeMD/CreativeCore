package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;

public class GuiLabel extends GuiControl{
	
	private String caption;
	private int captionWidth;
	public int color;
	
	
	public String getCaption()
	{
		return caption;
	}
	
	public void setCaption(String caption)
	{
		this.caption = caption;
		this.captionWidth = GuiRenderHelper.instance.getStringWidth(caption);
	}
	
	public GuiLabel(String caption, int x, int y)
	{
		this(caption, x, y, 14737632);
	}
	
	public GuiLabel(String caption, int x, int y, int color)
	{
		this(caption, x, y, GuiRenderHelper.instance.getStringWidth(caption), GuiRenderHelper.instance.getFontHeight(), color);
	}
	
	public GuiLabel(String caption, int x, int y, int width, int height, int color)
	{
		super(caption, x, y, width, height);
		this.color = color;
		setCaption(caption);
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		if(shouldDrawTitle())
			helper.font.drawStringWithShadow(caption, width/2-captionWidth/2, height/2-helper.getFontHeight()/2, getColor());
			//helper.font.drawStringWithShadow(title, 0, height/4, getColor());
	}
	
	@Override
	public boolean hasBorder()
	{
		return false;
	}
	
	@Override
	public boolean hasBackground()
	{
		return false;
	}
	
	public boolean shouldDrawTitle()
	{
		return true;
	}
	
	public int getColor()
	{
		return color;
	}

	

}
