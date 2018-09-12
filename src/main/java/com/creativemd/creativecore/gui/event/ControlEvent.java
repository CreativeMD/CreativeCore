package com.creativemd.creativecore.gui.event;

import com.creativemd.creativecore.gui.CoreControl;
import com.n247s.api.eventapi.eventsystem.EventType;

public abstract class ControlEvent extends EventType {
	
	public CoreControl source;
	
	public ControlEvent(CoreControl source) {
		this.source = source;
	}
	
}
