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
import net.minecraft.util.math.Vec3i;

public class GuiColorPlate extends GuiControl {
	
	private Color color;
	private DisplayStyle colorPlate;
	
	public GuiColorPlate(String name, int x, int y, int width, int height, Vec3i color) {
		super(name, x, y, width, height);
		
		this.marginWidth = 0;
		setColor(new Color(color.getX(), color.getY(), color.getZ()));
	}
	
	public void setColor(Vec3i color)
	{
		setColor(new Color((byte)color.getX(), (byte)color.getY(), (byte)color.getZ()));
	}
	
	public void setColor(Color color)
	{
		this.color = color;
		this.colorPlate = new ColoredDisplayStyle(color);
	}
	
	public Color getColor()
	{
		return color;
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
