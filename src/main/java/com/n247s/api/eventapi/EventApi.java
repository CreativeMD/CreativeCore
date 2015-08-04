package com.n247s.api.eventapi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EventApi
{
	public static final Logger logger = LogManager.getLogger("EventApi");
	public static EventBus instance = new EventBus();
	
	/**
	 * @param eventType
	 * @return - true if EventType is Canceled.
	 */
	public static boolean raiseEvent(Class<? extends EventType> eventType)
	{
		return instance.raiseEvent(eventType);
	}
	
	/**
	 * used to bind a custom CallHandler to a EventType.
	 * @param eventType
	 * @param callHandler
	 */
	public static void bindCallHandler(CallHandler callHandler)
	{
		instance.bindCallHandler(callHandler);
	}
	
	/**
	 * With this method you can add an EventListner(Class object/Class instance) which should be Called on eventRaise.
	 * 
	 * @param listner
	 * @throws IllegalArgumentException - If a CustomEventSubscribed Method contains more than one parameter,
	 * 		or if the parameter is not an instance of EventType.class.
	 */
	public static void RegisterEventListner(Object listner)
	{
		instance.RegisterEventListner(listner);
	}
	
	public static void RegisterEventListners(List<Object> listnersList)
	{
		instance.RegisterEventListners(listnersList);
	}
	
	/**
	 * With this method you can remove an EventListner(Class object/Class instance).
	 * 
	 * @param Listner
	 * @throws NullPointerException - if the EventListner isn't registered.
	 */
	public static void removeEventListner(Object Listner)
	{
		instance.removeEventListner(Listner);
	}
	
	public static void removeEventListners(List<Object> listnersList)
	{
		instance.removeEventListners(listnersList);
	}
	
	/**
	 * @param eventTypeClass
	 * @return The instance of the CallHandler of this specific EventType.
	 * 		Note that if no CallHanlder is assigned, a default instance is returned!
	 */
	public static CallHandler getCallHandlerFromEventType(Class<? extends EventType> eventTypeClass)
	{
		return instance.getCallHandlerFromEventType(eventTypeClass);
	}
}