package com.creativemd.creativecore.gui.controls.gui;

import javax.vecmath.Vector4d;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.Vec3d;

public class GuiColorPlate extends GuiControl {
	
	private Color color;
	private DisplayStyle colorPlate;
	
	public GuiColorPlate(String name, int x, int y, int width, int height, Vec3d color) {
		super(name, x, y, width, height);
		
		this.marginWidth = 0;
	}
	
	public void setColor(Vec3d color)
	{
		setColor(new Color((byte)color.xCoord, (byte)color.yCoord, (byte)color.zCoord));
	}
	
	public void setColor(Color color)
	{
		this.color = color;
		this.colorPlate = new ColoredDisplayStyle(color);
	}

	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		colorPlate.renderStyle(helper, width, height);
	}
	
	@Override
	public boolean hasBackground()
	{
		return false;
	}

}
