package com.creativemd.creativecore.common.gui.event;

import com.creativemd.creativecore.common.gui.controls.GuiControl;

public class ControlClickEvent extends GuiControlEvent{
	
	public int mouseX;
	public int mouseY;
	
	public ControlClickEvent(GuiControl source, int mouseX, int mouseY) {
		super(source);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

}
