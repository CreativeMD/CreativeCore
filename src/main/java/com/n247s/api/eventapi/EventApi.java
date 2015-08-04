package com.n247s.api.eventapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.n247s.api.eventapi.eventsystem.EventBus;

public class EventApi
{
	public static final Logger logger = LogManager.getLogger("EventApi");
	/** The instance of the EventApi, if you are not in the need to change the current behavior of the EventBus, use this instance to Bind/Register/call Callhandlers and EventTypes */
	public static final EventBus defaultInstance = new EventBus();
}
