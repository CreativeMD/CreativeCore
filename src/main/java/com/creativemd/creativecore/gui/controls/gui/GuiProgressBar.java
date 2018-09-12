package com.creativemd.creativecore.gui.controls.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;

public class GuiProgressBar extends GuiControl {

	public boolean showToolTip = true;

	public GuiProgressBar(String name, int x, int y, int width, int height, double max, double pos) {
		super(name, x, y, width, height);
		this.max = max;
		this.pos = pos;
		this.marginWidth = 0;
	}

	public double pos;
	public double max;

	public double getPercent() {
		return pos / max;
	}

	@Override
	public boolean hasMouseOverEffect() {
		return false;
	}

	@Override
	public ArrayList<String> getTooltip() {
		if (showToolTip) {
			ArrayList<String> toolTip = new ArrayList<>();
			toolTip.add(Math.round(this.pos) + "/" + Math.round(this.max) + " (" + (Math.round(getPercent() * 100)) + "%)");
			return toolTip;
		}
		return null;
	}

	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		double percent = getPercent();
		int progressWidth = (int) (width * percent);
		// if(progressWidth > 0)
		// style.getBorder(this).renderStyle(helper, progressWidth, height);
		if (progressWidth > 1)
			style.getFace(this).renderStyle(helper, progressWidth, height);
	}

}
