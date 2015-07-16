/**
 * This Class contains mainly methods to link through the KeyboardInput in Minecraft.
 * Because the given ForgeEvents aren't usable outside of an loaded world,
 * the KeyboardInput is monitored twice a tick, and linked to a custom KeyboardInputEventHandler.
 * Which is decent enough for guiControl, but isn't recommended for in-game use.
 * For in-game keyboardEventHandling, use the ForgeEvents instead!
 */
package com.creativemd.creativecore.common.gui.events;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.core.CreativeCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBoardEvents
{
	public static final KeyBoardEvents instance = new KeyBoardEvents();
	private static HashMap<Class, HashMap> methodList;
	private static GuiContainerSub mainGuiContainer;
	public static Logger log = CreativeCore.logger;
	
	public void addKeyboardListner(GuiControl guiControl)
	{
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
					methodList.get(currentMethod.getGenericParameterTypes()[0].getClass()).put(guiControl, currentMethod);
				}
				catch (Exception e)
				{
					log.catching(e);
				}
			}
		}
	}
	
	public void addMainGuiContianerKeyboardListner(GuiContainerSub guiContainer)
	{
		this.mainGuiContainer = guiContainer;
		refreshInstances();
	}
	
	private static void callEvents(Object eventType)
	{
		HashMap methodMap = methodList.get(eventType.getClass());
		Iterator iterator = methodMap.keySet().iterator();
		while(iterator.hasNext())
		{
			try
			{
				GuiControl currentMethodClass = (GuiControl) iterator.next();
				Method currentMethod = (Method) methodMap.get(currentMethodClass);
				
				currentMethod.invoke(currentMethodClass , eventType.getClass().newInstance());
			}
			catch(Exception e)
			{
				log.catching(e);
			}
		}
	}
	
	public static void refreshInstances()
	{
		methodList  = new HashMap<Class, HashMap>();
		methodList.put(KeyBoardEvents.onKeyPress.class, new HashMap<GuiControl, Method>());
		methodList.put(KeyBoardEvents.onKeyRelease.class, new HashMap<GuiControl, Method>());
	}
	
	public static class onKeyPress extends GuiEventHandler.DummyEventClass
	{
		public static int keyNumber;
		public static String keyName;
		
		protected static void callActionEvents(int key)
		{
			keyNumber = key;
			keyName = Keyboard.getKeyName(key);
			callEvents(new onKeyPress());
		}
	}
	
	public static class onKeyRelease extends GuiEventHandler.DummyEventClass
	{
		public static int keyNumber;
		public static String keyName;
		
		protected static void callActionEvents(int key)
		{
			keyNumber = key;
			keyName = Keyboard.getKeyName(key);
			callEvents(new onKeyRelease());
		}
	}
}
