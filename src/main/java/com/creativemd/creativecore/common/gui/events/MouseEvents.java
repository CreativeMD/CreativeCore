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
	private static HashMap<Class, HashMap> methodList;

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
	//left = 0, middle = 2, right = 1
	
	public void addMouseListner(GuiControl guiControl)
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
	
	public void addGuiContainerMouseListner(GuiContainerSub guiContainer)
	{
		this.mainGuiContainer = guiContainer;
		refreshInstanceList();
	}
	
    private static boolean isCtrlKeyDown()
    {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    private static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
	
	private static void callEvents(Object eventType)
	{
		superMousePosX = mouse.getEventX();
		superMousePosY = mouse.getEventY();
		superIsScrollingUp = superWheelScroll > 0 ? true : false;
		superIsScrollingDown = superWheelScroll < 0 ? true : false;
		
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
		
		superLastMousePosX = superMousePosX;
		superLastMousePosY = superMousePosY;
	}
	
	public static void refreshInstanceList()
	{
		methodList = new HashMap<Class, HashMap>();
		methodList.put(MouseEvents.onLeftClickEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onLeftMouseButtonPressEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onLeftMouseButtonReleaseEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onleftClickDragEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onDoubleLeftClickEvent.class, new HashMap<GuiControl, Method>());

		methodList.put(MouseEvents.onRightClickEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onRightMouseButtonPressEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onRightMouseButtonReleaseEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onRightClickDragEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onDoubleRightClickEvent.class, new HashMap<GuiControl, Method>());

		methodList.put(MouseEvents.onMouseButtonPressEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onMouseButtonReleaseEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onDoubleButtonClickEvent.class, new HashMap<GuiControl, Method>());

		methodList.put(MouseEvents.onWheelClickEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onWheelPressEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onWheelReleaseEvent.class, new HashMap<GuiControl, Method>());
		methodList.put(MouseEvents.onScrollEvent.class, new HashMap<GuiControl, Method>());

		methodList.put(MouseEvents.onMouseMoveEvent.class, new HashMap<GuiControl, Method>());
	}

	public static class onLeftClickEvent extends GuiEventHandler.DummyEventClass
	{
		public static int mousePosX = superMousePosX;
		public static int mousePosY = superMousePosY;

		protected static void callActionEvents()
		{
			callEvents(new onLeftClickEvent());
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
			callEvents(new onLeftMouseButtonPressEvent());
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
			callEvents(new onLeftMouseButtonReleaseEvent());
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
			callEvents(new onDoubleLeftClickEvent());
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
			callEvents(new onRightClickEvent());
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
			callEvents(new onRightMouseButtonPressEvent());
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
			callEvents(new onRightMouseButtonReleaseEvent());
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
			callEvents(new onDoubleRightClickEvent());
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
			callEvents(new onMouseButtonPressEvent());
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
			callEvents(new onMouseButtonReleaseEvent());
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
			callEvents(new onDoubleButtonClickEvent());
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
			callEvents(new onWheelClickEvent());
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
			callEvents(new onWheelPressEvent());
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
			callEvents(new onWheelReleaseEvent());
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
			callEvents(new onScrollEvent());
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
			callEvents(new onMouseMoveEvent());
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
			callEvents(new onleftClickDragEvent());
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
			callEvents(new onRightClickDragEvent());
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