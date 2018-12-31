package com.creativemd.creativecore.common.gui.controls.gui;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;

import net.minecraft.util.math.Vec3d;

public class GuiTimeline extends GuiParent {
	
	public double zoom = 1;
	
	public GuiTimeline(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<String> getTooltip() {
		Vec3d mouse = getMousePos();
		List<String> lines = new ArrayList<>();
		int tick = (int) (zoom * mouse.x);
		lines.add("" + tick + ". tick");
		return lines;
	}
	
}
