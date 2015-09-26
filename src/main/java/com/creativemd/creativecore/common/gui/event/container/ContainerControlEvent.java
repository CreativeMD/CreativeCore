package com.creativemd.creativecore.common.gui.event.container;

import com.creativemd.creativecore.common.container.slot.ContainerControl;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.n247s.api.eventapi.eventsystem.EventType;

public abstract class ContainerControlEvent extends EventType{
	
	public ContainerControl source;
	
	public ContainerControlEvent(ContainerControl source)
	{
		this.source = source;
	}
	
}
