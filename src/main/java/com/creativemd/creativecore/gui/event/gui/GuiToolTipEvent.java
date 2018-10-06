package com.creativemd.creativecore.gui.event.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.gui.GuiControl;

public class GuiToolTipEvent extends GuiControlEvent {
	
	public ArrayList<String> tooltip;
	
	public GuiToolTipEvent(ArrayList<String> tooltip, GuiControl source) {
		super(source);
		this.tooltip = tooltip;
	}
	
	@Override
	public boolean isCancelable() {
		return true;
	}
	
}
