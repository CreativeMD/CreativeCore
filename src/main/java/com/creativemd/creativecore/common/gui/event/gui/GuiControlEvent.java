package com.creativemd.creativecore.common.gui.event.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.event.ControlEvent;

public abstract class GuiControlEvent extends ControlEvent {
	
	public GuiControlEvent(GuiControl source) {
		super(source);
	}
	
}
