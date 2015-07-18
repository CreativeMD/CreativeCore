package com.creativemd.creativecore.common.gui.events;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.client.Minecraft;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.core.CreativeCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MouseEvents
{
	public static final MouseEvents instance = new MouseEvents();

	private static GuiContainerSub mainGuiContainer;
	private static Logger log = CreativeCore.logger;
	private static Mouse mouse;
	private static int superMousePosX;
	private static int superLastMousePosX;
	private static int superMousePosY;
	private static int superLastMousePosY;
	private static int superWheelScroll;
	private static boolean superIsScrollingUp;
	private static boolean superIsScrollingDown;
	
	public void addGuiContainerMouseListner(GuiContainerSub guiContainer)
	{
		this.mainGuiContainer = guiContainer;
	}
	
    private static boolean isCtrlKeyDown()
    {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    private static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
	
	private static void callEvents(Class<? extends GuiEventHandler.DummyEventClass> eventType)
	{
		superMousePosX = mouse.getEventX();
		superMousePosY = mouse.getEventY();
		superIsScrollingUp = superWheelScroll > 0 ? true : false;
		superIsScrollingDown = superWheelScroll < 0 ? true : false;
		
		if(mainGuiContainer != null)
			mainGuiContainer.callHandler.callEvents(eventType);
		else log.catching(new NullPointerException("mainGuiContainerGuiClass isn't registered!"));
		
		superLastMousePosX = superMousePosX;
		superLastMousePosY = superMousePosY;
	}
	
	public static class onLeftClickEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;

		protected static void callActionEvents()
		{
			callEvents(onLeftClickEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onLeftMouseButtonPressEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onLeftMouseButtonPressEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onLeftMouseButtonReleaseEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onLeftMouseButtonReleaseEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onDoubleLeftClickEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onDoubleLeftClickEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onRightClickEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;

		protected static void callActionEvents()
		{
			callEvents(onRightClickEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onRightMouseButtonPressEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onRightMouseButtonPressEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onRightMouseButtonReleaseEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onRightMouseButtonReleaseEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onDoubleRightClickEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onDoubleRightClickEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onMouseButtonPressEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		public static int mouseButtonID;
		
		protected static void callActionEvents(int mousebuttonID)
		{
			mouseButtonID = mousebuttonID;
			callEvents(onMouseButtonPressEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onMouseButtonReleaseEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		public static int mouseButtonID;
		
		protected static void callActionEvents(int mousebuttonID)
		{
			mouseButtonID = mousebuttonID;
			callEvents(onMouseButtonReleaseEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onDoubleButtonClickEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		public static int mouseButtonID;
		
		protected static void callActionEvents(int mousebuttonID)
		{
			mouseButtonID = mousebuttonID;
			callEvents(onDoubleButtonClickEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onWheelClickEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onWheelClickEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onWheelPressEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onWheelPressEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onWheelReleaseEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		
		protected static void callActionEvents()
		{
			callEvents(onWheelReleaseEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onScrollEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		public static int wheelScroll = superWheelScroll;
		
		protected static void callActionEvents(int wheel)
		{
			superWheelScroll = wheel;
			callEvents(onScrollEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onMouseMoveEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		public static int lastMousePosX = superLastMousePosX;
		public static int lastMousePosY = superLastMousePosY;
		public static boolean isScrollingup = superIsScrollingUp;
		public static boolean isScrollingdown = superIsScrollingDown;
		
		protected static void callActionEvents()
		{
			callEvents(onMouseMoveEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onleftClickDragEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		public static int lastMousePosX = superLastMousePosX;
		public static int lastMousePosY = superLastMousePosY;
		public static int wheelScroll = superWheelScroll;
		public static boolean isScrollingup = superIsScrollingUp;
		public static boolean isScrollingdown = superIsScrollingDown;
		
		protected static void callActionEvents()
		{
			callEvents(onleftClickDragEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onRightClickDragEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		public static int lastMousePosX = superLastMousePosX;
		public static int lastMousePosY = superLastMousePosY;
		public static int wheelScroll = superWheelScroll;
		public static boolean isScrollingup = superIsScrollingUp;
		public static boolean isScrollingdown = superIsScrollingDown;
		
		protected static void callActionEvents()
		{
			callEvents(onRightClickDragEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
	
	public static class onMouseButtonPressDragEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;
		public static int lastMousePosX = superLastMousePosX;
		public static int lastMousePosY = superLastMousePosY;
		public static int wheelScroll = superWheelScroll;
		public static boolean isScrollingup = superIsScrollingUp;
		public static boolean isScrollingdown = superIsScrollingDown;
		
		protected static void callActionEvents()
		{
			callEvents(onMouseButtonPressDragEvent.class);
		}
		
		/**
		 * Returns true if either shift key is down
		 */
		public boolean isShiftPressed()
		{
			return isShiftKeyDown();
		}
		
		/**
		 * Returns true if either windows ctrl key is down or if either mac meta key is down
		 */
		public boolean isCtrlPressed()
		{
			return isCtrlKeyDown();
		}
	}
}