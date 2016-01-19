package com.creativemd.creativecore.common.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.core.CreativeCore;
import com.n247s.api.eventapi.EventApi;
import com.n247s.api.eventapi.eventsystem.CallHandler;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;
import com.n247s.api.eventapi.eventsystem.EventApiCallHandler;
import com.n247s.api.eventapi.eventsystem.EventBus;
import com.n247s.api.eventapi.eventsystem.EventType;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeCoreEventBus extends EventBus
{
	public ArrayList<EventType> eventsToRaise = new ArrayList<EventType>();

	/**
	 * Be very careful when creating your own EventBus! Only create one when you
	 * really need to, since this is the most sensitive part of the
	 * CustomEventSystem, messing up the system is rather easy achievable.
	 */
	public CreativeCoreEventBus() {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			initClient();
		else
			initServer();
	}

	public void initServer() {
		TickHandler.ServerEvents.add(this);
	}

	@SideOnly(Side.CLIENT)
	public void initClient() {
		TickHandler.ClientEvents.add(this);
	}

	/**
	 * @param eventType
	 * @param force
	 * @return - true if EventType is Canceled.
	 */
	public boolean raiseEvent(EventType event, boolean force) {
		if (event.isCancelable() || force) {
			if (!this.EventList.containsKey(event.getClass()))
				this.EventList.put(event.getClass(), new CreativeCoreCallHandler(
						event.getClass()));
			return ((CreativeCoreCallHandler) this.EventList.get(event.getClass())).CallInstances(event);
		}
		eventsToRaise.add(event);
		return false;
	}
	
	@Override
	public boolean raiseEvent(EventType eventType)
	{
		try
		{
			Class eventClass =  eventType.getClass();
			if(!this.EventList.containsKey(eventClass))
				this.EventList.put(eventClass, new CreativeCoreCallHandler(eventClass));
			return ((CreativeCoreCallHandler)this.EventList.get(eventClass)).CallInstances(eventType);
		}
		catch(Exception e)
		{
			CreativeCore.logger.catching(e);
		}
		return false;
	}

	/**
	 * used to bind a custom CallHandler to a EventType.
	 * 
	 * @param eventType
	 * @param callHandler
	 */
	public void bindCreativeCoreCallHandler(CreativeCoreCallHandler callHandler) {
		if (this.EventList.containsKey(callHandler.getEventType()))
			this.EventList.remove(callHandler.getEventType());
		this.EventList.put(callHandler.getEventType(), callHandler);
	}

	/**
	 * Destroy the eventbus
	 */
	public void removeAllEventListeners() {
		for (Object value : this.EventList.values()) {
			((CreativeCoreCallHandler)value).getInstanceMap().clear();
		}
		EventList.clear();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			removeTickEventClient();
		else
			removeTickEventServer();
	}

	public void removeTickEventServer() {
		TickHandler.ServerEvents.remove(this);
	}

	@SideOnly(Side.CLIENT)
	public void removeTickEventClient() {
		TickHandler.ClientEvents.remove(this);
	}
	
	@Override
	public CallHandler getCallHandlerFromEventType(Class<? extends EventType> eventTypeClass)
	{
		if(!this.EventList.containsKey(eventTypeClass))
			this.EventList.put(eventTypeClass, new CreativeCoreCallHandler(eventTypeClass));
		return (CallHandler) this.EventList.get(eventTypeClass);
	}
}
