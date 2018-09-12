package com.creativemd.creativecore.event;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;
import com.n247s.api.eventapi.eventsystem.EventApiCallHandler;
import com.n247s.api.eventapi.eventsystem.EventType;

public class CreativeCoreCallHandler extends EventApiCallHandler {
	public CreativeCoreCallHandler(Class<? extends EventType> eventType) {
		super(eventType);
	}

	@Override
	protected boolean CallInstances(EventType eventType) {
		return super.CallInstances(eventType);
	}

	public Class<? extends EventType> getEventType() {
		return this.eventType;
	}

	public HashMap<CustomEventSubscribe.Priority, LinkedHashMap> getInstanceMap() {
		return this.instanceMap;
	}
}
