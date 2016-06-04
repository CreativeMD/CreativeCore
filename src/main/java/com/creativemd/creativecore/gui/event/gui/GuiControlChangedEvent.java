package com.creativemd.creativecore.gui.event.gui;

import com.creativemd.creativecore.gui.GuiControl;

public class GuiControlChangedEvent extends GuiControlEvent{

	public GuiControlChangedEvent(GuiControl source) {
		super(source);
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

}
