package com.creativemd.creativecore.common.gui.event.gui;

import java.util.List;

import com.creativemd.creativecore.common.gui.GuiControl;

public class GuiToolTipEvent extends GuiControlEvent {
	
	public List<String> tooltip;
	
	public GuiToolTipEvent(List<String> tooltip, GuiControl source) {
		super(source);
		this.tooltip = tooltip;
	}
	
	@Override
	public boolean isCancelable() {
		return true;
	}
	
}
