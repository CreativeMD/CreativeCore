package com.creativemd.creativecore.common.gui.event;

import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.n247s.api.eventapi.eventsystem.EventType;

public abstract class GuiControlEvent extends EventType{
	
	public GuiControl source;
	
	public GuiControlEvent(GuiControl source)
	{
		this.source = source;
	}
	
}
