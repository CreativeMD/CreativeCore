package com.creativemd.creativecore.common.gui.client.style;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

public class SidedColoredDisplayStyle extends DisplayStyle {
	
	public Color north;
	public Color east;
	public Color south;
	public Color west;
	
	public SidedColoredDisplayStyle(Color north, Color east, Color south, Color west) {
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
	}
	
	@Override
	public void renderStyle(GuiRenderHelper helper, int width, int height) {
		int middleX = width / 2;
		int middleY = height / 2;
		helper.renderColorTriangle(0, 0, width, 0, middleX, middleY, north);
		helper.renderColorTriangle(middleX, middleY, width, 0, width, height, east);
		helper.renderColorTriangle(0, height, middleX, middleY, width, height, south);
		helper.renderColorTriangle(middleX, middleY, 0, height, 0, 0, west);
		// helper.renderColorPlate(color, width, height);
	}
	
}
