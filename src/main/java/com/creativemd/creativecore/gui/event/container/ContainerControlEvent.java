package com.creativemd.creativecore.gui.event.container;

import com.creativemd.creativecore.gui.ContainerControl;
import com.creativemd.creativecore.gui.event.ControlEvent;
import com.n247s.api.eventapi.eventsystem.EventType;

public abstract class ContainerControlEvent extends ControlEvent{
	
	public ContainerControlEvent(ContainerControl source)
	{
		super(source);
	}
	
}
