package team.creative.creativecore.common.config.gui;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiConfigSubControl extends GuiParent {
	
	public GuiConfigSubControl(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}
	
	@Override
	public ControlFormatting getControlFormatting() {
		return ControlFormatting.TRANSPARENT;
	}
}
