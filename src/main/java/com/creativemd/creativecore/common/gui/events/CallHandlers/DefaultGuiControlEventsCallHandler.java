package com.creativemd.creativecore.common.gui.events.CallHandlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.gui.events.GuiEventHandler;
import com.creativemd.creativecore.common.gui.events.KeyBoardEvents;
import com.creativemd.creativecore.common.gui.events.MouseEvents;
import com.creativemd.creativecore.common.gui.events.SubscribeGuiInputEvent;
import com.creativemd.creativecore.common.gui.events.GuiEventHandler.DummyEventClass;
import com.creativemd.creativecore.common.gui.events.KeyBoardEvents.onDoubleKeyPress;
import com.creativemd.creativecore.common.gui.events.KeyBoardEvents.onKeyPress;
import com.creativemd.creativecore.common.gui.events.KeyBoardEvents.onKeyRelease;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onDoubleButtonClickEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onDoubleLeftClickEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onDoubleRightClickEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onLeftClickEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onLeftMouseButtonPressEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onLeftMouseButtonReleaseEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onMouseButtonPressEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onMouseButtonReleaseEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onMouseMoveEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onRightClickDragEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onRightClickEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onRightMouseButtonPressEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onRightMouseButtonReleaseEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onScrollEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onWheelClickEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onWheelPressEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onWheelReleaseEvent;
import com.creativemd.creativecore.common.gui.events.MouseEvents.onleftClickDragEvent;
import com.creativemd.creativecore.core.CreativeCore;

public class DefaultGuiControlEventsCallHandler extends GuiEventCallHandler
{
	private static final Logger log = CreativeCore.logger;
	/** private for now since it requires some extensive Handling, for a ClassList use getCurrentRegisteredGuiControlList(). */
	private static HashMap<Class, LinkedHashMap> EventCallList;
	/** This can be used as well, though it needs some handling from the SubGuiClass! */
	public static List<GuiControl> guiControlClassList = new ArrayList<GuiControl>();
	
	@Override
	public void addSingleGuiControlListner(Object guiControl)
	{
		if(!(guiControl instanceof GuiControl))
			log.catching(new IllegalArgumentException("Current object isn't an instance of GuiControl."));
		Method[] method = guiControl.getClass().getMethods();

		for (int j = 0; j < method.length; j++)
		{
			Method currentMethod = method[j];

			if (currentMethod.isAnnotationPresent(SubscribeGuiInputEvent.class))
			{
				if (currentMethod.getParameterTypes().length != 1 || !(currentMethod.getGenericParameterTypes()[0] instanceof GuiEventHandler.DummyEventClass))
					log.catching(new IllegalArgumentException("Couldn't resolve parameters of:" + currentMethod.getDeclaringClass() + ";" + currentMethod.getName()));
				try
				{
					EventCallList.get(currentMethod.getGenericParameterTypes()[0].getClass()).put(guiControl, currentMethod);
				}
				catch (Exception e)
				{
					log.catching(e);
				}
			}
		}
	}
	
	public void addGuiControlListnersList(ArrayList<GuiControl> list)
	{
		for(int i = 0; i > list.size(); i++)
		{
			addSingleGuiControlListner(list.get(i));
		}
	}
	
	public List getCurrentRegisteredGuiControlList()
	{
		Iterator mainMapIterator = EventCallList.keySet().iterator();
		List ClassList = new ArrayList();
		
		while(mainMapIterator.hasNext())
		{
			LinkedHashMap subHashMap = EventCallList.get(mainMapIterator.next());
			Iterator subMapIterator = subHashMap.keySet().iterator();
			
			while(subMapIterator.hasNext())
			{
				GuiControl currentGuiControlClass = (GuiControl) subHashMap.get(subMapIterator.next());
				if(!ClassList.contains(currentGuiControlClass))
					ClassList.add(currentGuiControlClass);
			}
		}
		if(ClassList.isEmpty())
			return null;
		return ClassList;
	}
	
	/** This will Call everyClass in reversed addition order*/
	@Override
	public void callEvents(Class<? extends GuiEventHandler.DummyEventClass> eventType)
	{
		LinkedHashMap methodMap = EventCallList.get(eventType.getClass());
		
		for(int i = methodMap.size(); i > 0; --i)
		{
			try
			{
				GuiControl currentMethodClass = (GuiControl) methodMap.get(i);
				Method currentMethod = (Method) methodMap.get(currentMethodClass);
			
				currentMethod.invoke(currentMethodClass , eventType.getClass().newInstance());
			}
			catch(Exception e)
			{
				log.catching(e);
			}
		}
	}
	
	public void CallEventsFromList(Class<? extends GuiEventHandler.DummyEventClass> eventType)
	{
		if(guiControlClassList != null && guiControlClassList.isEmpty())
		{
			for(int i = guiControlClassList.size(); i > 0; --i)
			{
				//Here you can put all the methods for the GuiControl. might be a singleMainMethod, splitting it up into other event-methods.
				//guiControlClassList.get(i).callEvent;
			}
		}
	}
	
	/** This will reset all the Stored Lists. e.g. Clear all registered instances.*/
	public static void refreshGuiControlInstanceList()
	{
		/*
		 * MouseEvents
		 */
		EventCallList = new HashMap<Class, LinkedHashMap>();
		EventCallList.put(MouseEvents.onLeftClickEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onLeftMouseButtonPressEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onLeftMouseButtonReleaseEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onleftClickDragEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onDoubleLeftClickEvent.class, new LinkedHashMap<GuiControl, Method>());

		EventCallList.put(MouseEvents.onRightClickEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onRightMouseButtonPressEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onRightMouseButtonReleaseEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onRightClickDragEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onDoubleRightClickEvent.class, new LinkedHashMap<GuiControl, Method>());

		EventCallList.put(MouseEvents.onMouseButtonPressEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onMouseButtonReleaseEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onDoubleButtonClickEvent.class, new LinkedHashMap<GuiControl, Method>());

		EventCallList.put(MouseEvents.onWheelClickEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onWheelPressEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onWheelReleaseEvent.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(MouseEvents.onScrollEvent.class, new LinkedHashMap<GuiControl, Method>());

		EventCallList.put(MouseEvents.onMouseMoveEvent.class, new LinkedHashMap<GuiControl, Method>());
		
		/*
		 * KeyboardEvents
		 */
		EventCallList.put(KeyBoardEvents.onKeyPress.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(KeyBoardEvents.onKeyRelease.class, new LinkedHashMap<GuiControl, Method>());
		EventCallList.put(KeyBoardEvents.onDoubleKeyPress.class, new LinkedHashMap<GuiControl, Method>());
	}
}
