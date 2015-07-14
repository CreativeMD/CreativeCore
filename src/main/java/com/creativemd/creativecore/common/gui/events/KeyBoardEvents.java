/**
 * This Class contains mainly methods to link through the KeyboardInput in Minecraft.
 * Because the given ForgeEvents aren't usable outside of an loaded world,
 * the KeyboardInput is monitored twice a tick, and linked to a custom KeyboardInputEventHandler.
 * Which is decent enough for guiControl, but isn't recommended for in-game use.
 * For in-game keyboardEventHandling, use the ForgeEvents instead!
 */
package com.creativemd.creativecore.common.gui.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
	private static List<GuiControl> KeyBoardEventListnersList = new ArrayList<GuiControl>();
	private static GuiContainerSub mainGuiContainer;
	public static Logger log = CreativeCore.logger;
	
	public void addKeyboardListner(GuiControl guiControl)
	{
		KeyBoardEventListnersList.add(guiControl);
	}
	
	public void addMainGuiContianerKeyboardListner(GuiContainerSub guiContainer)
	{
		this.mainGuiContainer = guiContainer;
	}
	
	private static void callEvents(Object eventType)
	{
		for(int i = 0; i < KeyBoardEventListnersList.size(); i++)
		{
			Class clazz = KeyBoardEventListnersList.get(i).getClass();
			Method[] method = clazz.getMethods();
			
			for(int j = 0; j < method.length; i++)
			{
				Method currentMethod = method[j];
				
				if(currentMethod.isAnnotationPresent(SubscribeGuiInputEvent.class))
				{
					if(currentMethod.getParameterTypes().length != 1 || !(currentMethod.getGenericParameterTypes()[0] instanceof KeyBoardEvents))
						throw new IllegalArgumentException("Couldn't resolve parameters of:" + currentMethod.getDeclaringClass() + ";" + currentMethod.getName());
					
					if(currentMethod.getGenericParameterTypes()[0].getClass() == eventType.getClass())
					{
						try
						{
							currentMethod.invoke(KeyBoardEventListnersList.get(i), eventType.getClass());
						}
						catch(Exception e)
						{
							log.catching(e);
						}
					}
				}
			}
		}
	}
	
	public static class onKeyPress
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
	
	public static class onKeyDown
	{
		public static int keyNumber;
		public static String keyName;
		
		protected static void callActionEvents(int key)
		{
			keyNumber = key;
			keyName = Keyboard.getKeyName(key);
			callEvents(new onKeyDown());
		}
	}
	
	public static class onKeyRelease
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
