package com.creativemd.creativecore.gui;

import org.lwjgl.util.Color;

public class Rect {

	public int minX;
	public int minY;
	public int maxX;
	public int maxY;

	public Rect(Rect rect) {
		this(rect.minX, rect.minY, rect.maxX, rect.maxY);
	}

	public Rect(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public Rect mergeRects(Rect otherRect) {
		return new Rect(Math.max(this.minX, otherRect.minX), Math.max(this.minY, otherRect.minY), Math.min(this.maxX, otherRect.maxX), Math.min(this.maxY, otherRect.maxY));
	}

	public void renderRect(GuiRenderHelper helper, Color color) {
		helper.renderColorPlate(minX, minY, color, maxX, maxY);
	}

	public Rect getOffsetRect(int x, int y) {
		return new Rect(minX + x, minY + y, maxX + x, maxY + y);
	}

}
