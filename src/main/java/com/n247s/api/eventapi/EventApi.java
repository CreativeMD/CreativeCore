package com.n247s.api.eventapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import com.forgedevelopment.lib.classloading.SubApiAnnotations.SubApi;
import com.n247s.api.eventapi.eventsystem.EventBus;

//import cpw.mods.fml.common.Mod.Instance;

//@SubApi(APIID="EventApi", APIVersion="1.0.0")
public class EventApi {
	public static final Logger logger = LogManager.getLogger("EventApi");
	/**
	 * The instance of the EventApi, if you are not in the need to change the
	 * current behavior of the EventBus, use this instance to Bind/Register/call
	 * Callhandlers and EventTypes
	 */
	public static final EventBus defaultEventBusInstance = new EventBus();

	// @Instance
	// public static EventApi instance;
}
