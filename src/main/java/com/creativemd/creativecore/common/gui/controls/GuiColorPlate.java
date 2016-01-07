package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector4d;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.Vec3;

public class GuiColorPlate extends GuiControl {
	
	public Vec3 color;
	
	public GuiColorPlate(String name, int x, int y, int width, int height, Vec3 color) {
		super(name, x, y, width, height);
		this.color = color;
	}

	@Override
	public void drawControl(FontRenderer renderer) {
		Vector4d black = new Vector4d(0, 0, 0, 255);
		RenderHelper2D.drawGradientRect(0, 0, this.width, this.height, black, black);
		
		Vector4d plate = new Vector4d(color.xCoord, color.yCoord, color.zCoord, 255);
		RenderHelper2D.drawGradientRect(1, 1, this.width-1, this.height-1, plate, plate);
	}

}
