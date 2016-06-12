package com.creativemd.creativecore.gui.controls.gui;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.mc.GuiContainerSub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class GuiProgressBar extends GuiControl{
	
	public GuiProgressBar(String name, int x, int y, int width, int height, double max, double pos) {
		super(name, x, y, width, height);
		this.max = max;
		this.pos = pos;
		this.marginWidth = 0;
	}
	
	public double pos;
	public double max;
	
	public double getPercent()
	{
		return pos/max;
	}
	
	@Override
	public boolean hasMouseOverEffect()
	{
		return false;
	}

	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		double percent = getPercent();
		int progressWidth = (int) (width*percent);
		if(progressWidth > 0)
			style.getBorder(this).renderStyle(helper, progressWidth, height);
		if(progressWidth > 1)
			style.getFace(this).renderStyle(helper, progressWidth-1, height);
	}

}
