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
	private static GuiContainerSub mainGuiContainer;
	public static Logger log = CreativeCore.logger;
	
	public void addMainGuiContianerKeyboardListner(GuiContainerSub guiContainer)
	{
		this.mainGuiContainer = guiContainer;
	}
	
	private static void callEvents(Class<? extends GuiEventHandler.DummyEventClass> eventType)
	{
		if(mainGuiContainer != null)
			mainGuiContainer.callHandler.callEvents(eventType);
		else log.catching(new NullPointerException("mainGuiContainerGuiClass isn't registered!"));
	}
	
	public static class onKeyPress extends GuiEventHandler.DummyEventClass
	{
		public static int keyNumber;
		public static String keyName;
		
		protected static void callActionEvents(int key)
		{
			keyNumber = key;
			keyName = Keyboard.getKeyName(key);
			callEvents(onKeyPress.class);
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
			callEvents(onKeyRelease.class);
		}
	}
	
	public static class onDoubleKeyPress extends GuiEventHandler.DummyEventClass
	{
		public static int keyNumber;
		public static String keyName;
		
		protected static void callActionEvents(int key)
		{
			keyNumber = key;
			keyName = Keyboard.getKeyName(key);
			callEvents(onKeyRelease.class);
		}
	}
}
