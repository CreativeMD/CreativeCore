package com.n247s.api.eventapi.eventsystem;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.n247s.api.eventapi.EventApi;


/**
 * The default CallHandler of the EventApi.<br>
 * Its recommended to use this class for a custom CallHanlder, since it has pre-made CallMethods at your disposal.
 */
public class EventApiCallHandler extends CallHandler
{
	private static final Logger log = EventApi.logger;
	private EventType eventTypeInstance = null;
	
	public EventApiCallHandler(Class<? extends EventType> eventType)
	{
		super(eventType);
	}

	/**
	 * By default this version of CallIntances will be used, though when you want to use a using a Custom CallHandler,
	 * you can Extend this Class to use the other preMade Methods.
	 */
	@Override
	protected boolean CallInstances(EventType eventType)
	{
		boolean isCanceled = false;
		try
		{
			this.eventTypeInstance = eventType;
			isCanceled = CallInstancesInOrder(this.eventTypeInstance);
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return isCanceled;
	}
	
	/**
	 * @param blackList - List with Classes/instances that should not be Called.
	 */
	protected boolean CallInstancesWithBlackList(EventType eventTypeInstance, List blackList)
	{
		try
		{
			for(int i = 0; i < 5; i++)
			{
				HashMap linkedHashMap = instanceMap.get(CustomEventSubscribe.Priority.getPriorityInOrder(i));
				
				if(linkedHashMap == null)
					continue;
				
				Iterator iterator = linkedHashMap.keySet().iterator();
				while(iterator.hasNext())
				{
					Object originalEntry = iterator.next();
					if(blackList.contains(originalEntry))
					{
						blackList.remove(originalEntry);
						continue;
					}
					((Method) linkedHashMap.get(originalEntry)).invoke(originalEntry, eventTypeInstance);
				}
			}
			if(blackList.size() > 0)
				log.error("Not all Objects from the blackList can be resolved!, report this to the modAuthor!");
			
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return eventTypeInstance.isCanceled();
	}
	
	protected boolean CallInstancesInOrder(EventType eventTypeInstance)
	{
		try
		{
			for(int i = 0; i < 5; i++)
			{
				LinkedHashMap linkedHashMap = instanceMap.get(CustomEventSubscribe.Priority.getPriorityInOrder(i));
				
				if(linkedHashMap == null)
					continue;
				
				Iterator iterator = linkedHashMap.keySet().iterator();
				while(iterator.hasNext())
				{
					Object originalEntry = iterator.next();
						((Method) linkedHashMap.get(originalEntry)).invoke(originalEntry, eventTypeInstance);
				}
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return eventTypeInstance.isCanceled();
	}
	
	/**
	 * @param orderList - List with Classes and instances that should be called in a the specific given order(ascending).
	 * 		Note that all Classes/instances that are not included in the list will be called afterwards in order of registering.
	 * 		(In other words, giving an empty list(null) will cause all Classes/instances being called in registering order)
	 * @return 
	 * @throws IllegalArgumentException - If a CustomEventSubscribed Method contains more than one parameter,
	 * 		or if the parameter is not an instance of EventType.class.
	 */
	protected boolean CallInstancesInOrder(EventType eventTypeInstance, List orderList)
	{
		List calledInstanceList = new ArrayList();
		
		if(orderList != null)
		{
			try
			{
				boolean containsMethod = false;
				for(int i = 0; i < orderList.size(); i++)
				{
					Object originalEntry = orderList.get(i);
					if(!this.entryCheckList.contains(originalEntry))
						continue;
					
					Class clazz;
					if (!(originalEntry instanceof Class))
							clazz = originalEntry.getClass();
					else clazz = (Class) originalEntry;

					Method[] methodArray = clazz.getMethods();
					for (int j = 0; j < methodArray.length; j++)
					{
						Method currentMethod = methodArray[j];

						if (!currentMethod.isAnnotationPresent(CustomEventSubscribe.class))
							continue;

						if (currentMethod.getParameterTypes().length > 1)
							log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't have more than one Parameter!"));
						if (originalEntry instanceof Class && !Modifier.isStatic(currentMethod.getModifiers()))
							log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't be non-static if you register an Class Object!"));

						if (eventType.isAssignableFrom(currentMethod.getParameterTypes()[0]))
						{
							currentMethod.invoke(originalEntry, eventTypeInstance);
							containsMethod = true;
						}
						else log.catching(new IllegalArgumentException("The Parameter of a CustomEventSubscribed method isn't an EventType!"));
					}
					if (containsMethod)
						containsMethod = false;
					else log.catching(new IllegalArgumentException("Class " + clazz.getName() + " doesn't contain an eventMethod!"));
				}
				calledInstanceList.addAll(orderList);
			}
			catch (Exception e)
			{
				log.catching(e);
			}
		}
		else
		{
			CallInstancesInOrder(eventTypeInstance);
		}
		return eventTypeInstance.isCanceled();
	}
	
	/**
	 * @param orderList - List with Classes/instances that should be called in a the specific given order(ascending).
	 * 		Note that all Classes/instances that are not included in the list will be called afterwards in order of registering.
	 * 		(In other words, giving an empty list(null) will cause all Classes/instances being called in registering order)
	 * @param blackList - List with Class objects and instances that should not be Called.
	 * @throws IllegalArgumentException - If a CustomEventSubscribed Method contains more than one parameter,
	 * 		or if the parameter is not an instance of EventType.class.
	 */
	protected boolean CallInstancesInOrderWithBlackList(EventType eventTypeInstance, List orderList, List blackList)
	{
		List calledInstanceList = new ArrayList();
		
		for(int i = 0; i < blackList.size(); i++)
		{
			Object current = blackList.get(i);
			if(orderList.contains(current))
				orderList.remove(current);
		}
		
		if(orderList != null)
		{
			try
			{
				boolean containsMethod = false;
				for(int i = 0; i < orderList.size(); i++)
				{
					Object originalEntry = orderList.get(i);
					if(!this.entryCheckList.contains(originalEntry))
						continue;
					
					Class clazz;
					if (!(originalEntry instanceof Class))
						clazz = originalEntry.getClass();
					else clazz = (Class) originalEntry;

					Method[] methodArray = clazz.getMethods();
					for (int j = 0; j < methodArray.length; j++)
					{
						Method currentMethod = methodArray[j];

						if (!currentMethod.isAnnotationPresent(CustomEventSubscribe.class))
							continue;

						if (currentMethod.getParameterTypes().length > 1)
							log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't have more than one Parameter!"));
						if (originalEntry instanceof Class && !Modifier.isStatic(currentMethod.getModifiers()))
							log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't be non-static if you register an Class Object!"));

						if (eventType.isAssignableFrom(currentMethod.getParameterTypes()[0]))
						{
							currentMethod.invoke(originalEntry, eventTypeInstance);
							containsMethod = true;
						}
						else log.catching(new IllegalArgumentException("The Parameter of a CustomEventSubscribed method isn't an EventType!"));
					}
					if (containsMethod)
						containsMethod = false;
					else log.catching(new IllegalArgumentException("Class " + clazz.getName() + " doesn't contain an eventMethod!"));

				}
				calledInstanceList.addAll(orderList);
			}
			catch(Exception e)
			{
				log.catching(e);
			}
		}
		else
		{
			CallInstancesWithBlackList(eventTypeInstance, blackList);
		}
		if(blackList.size() > 0)
			calledInstanceList.addAll(blackList);
		
		return eventTypeInstance.isCanceled();
	}
	
	/**
	 * 
	 * @param whiteList
	 */
	protected boolean CallInstancesWithWhiteList(EventType eventTypeInstance, List whiteList)
	{
		try
		{
			boolean containsMethod = false;
			
			for(int i = 0; i < whiteList.size(); i++)
			{
				Object originalEntry = whiteList.get(i);
				if(!this.entryCheckList.contains(originalEntry))
					continue;
				
				Class clazz;
				if(!(originalEntry instanceof Class))
					clazz = originalEntry.getClass();
				else clazz = (Class)originalEntry;
				
				Method[] methodArray = clazz.getMethods();
				
				for(int j = 0; j < methodArray.length; j++)
				{
					Method currentMethod = methodArray[j];
					
					if(!currentMethod.isAnnotationPresent(CustomEventSubscribe.class))
						continue;
					
					if (currentMethod.getParameterTypes().length > 1)
						log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't have more than one Parameter!"));
					if (originalEntry instanceof Class && !Modifier.isStatic(currentMethod.getModifiers()))
						log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't be non-static if you register an Class Object!"));

					if (eventType.isAssignableFrom(currentMethod.getParameterTypes()[0]))
					{
						currentMethod.invoke(originalEntry, eventTypeInstance);
						containsMethod = true;
					}
					else log.catching(new IllegalArgumentException("The Parameter of a CustomEventSubscribed method isn't an EventType!"));
				}
				if (containsMethod)
					containsMethod = false;
				else log.catching(new IllegalArgumentException("Class " + clazz.getName() + " doesn't contain an eventMethod!"));
			}
		}
		catch(Exception e)
		{
			log.catching(e);
		}
		return eventTypeInstance.isCanceled();
	}
}
