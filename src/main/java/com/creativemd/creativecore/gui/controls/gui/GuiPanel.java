package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.GuiParent;

import net.minecraft.client.renderer.GlStateManager;

public class GuiPanel extends GuiParent {
	
	public static Style panelStyle = new Style("panel", new ColoredDisplayStyle(0, 0, 0, 140), DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay);
	
	public GuiPanel(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}
	
	@Override
	protected void renderBackground(GuiRenderHelper helper, Style style) {
		GlStateManager.pushMatrix();
		DisplayStyle display = panelStyle.getBorder(this);
		display.renderStyle(helper, width, borderWidth);
		display.renderStyle(helper, borderWidth, height);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, height - borderWidth, 0);
		display.renderStyle(helper, width, borderWidth);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(width - borderWidth, 0, 0);
		display.renderStyle(helper, borderWidth, height);
		GlStateManager.popMatrix();
		
		GlStateManager.translate(borderWidth, borderWidth, 0);
	}
	
}
