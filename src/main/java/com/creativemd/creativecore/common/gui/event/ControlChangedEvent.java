package com.creativemd.creativecore.common.gui.event;

import com.creativemd.creativecore.common.gui.controls.GuiControl;

public class ControlChangedEvent extends GuiControlEvent{

	public ControlChangedEvent(GuiControl source) {
		super(source);
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

}
