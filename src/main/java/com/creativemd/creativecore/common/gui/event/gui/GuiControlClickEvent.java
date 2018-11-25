package com.creativemd.creativecore.common.gui.event.gui;

import com.creativemd.creativecore.common.gui.GuiControl;

public class GuiControlClickEvent extends GuiControlEvent {
	
	public int mouseX;
	public int mouseY;
	
	public GuiControlClickEvent(GuiControl source, int mouseX, int mouseY) {
		super(source);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
	
	@Override
	public boolean isCancelable() {
		return false;
	}
	
}
