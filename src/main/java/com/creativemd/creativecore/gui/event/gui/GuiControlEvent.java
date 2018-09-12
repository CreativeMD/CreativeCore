package com.creativemd.creativecore.gui.event.gui;

import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.event.ControlEvent;

public abstract class GuiControlEvent extends ControlEvent {

	public GuiControlEvent(GuiControl source) {
		super(source);
	}

}
