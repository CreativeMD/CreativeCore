package com.creativemd.creativecore.common.gui.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private static HashMap<Class, List> methodList;
	static
	{
		methodList  = new HashMap<Class, List>();
		methodList.put(MouseEvents.onLeftClickEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onLeftMouseButtonPressEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onLeftMouseButtonReleaseEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onleftClickDragEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onDoubleLeftClickEvent.class, new ArrayList<Method>());

		methodList.put(MouseEvents.onRightClickEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onRightMouseButtonPressEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onRightMouseButtonReleaseEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onRightClickDragEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onDoubleRightClickEvent.class, new ArrayList<Method>());
		
		methodList.put(MouseEvents.onMouseButtonPressEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onMouseButtonReleaseEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onDoubleButtonClickEvent.class, new ArrayList<Method>());
		
		methodList.put(MouseEvents.onWheelClickEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onWheelPressEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onWheelReleaseEvent.class, new ArrayList<Method>());
		methodList.put(MouseEvents.onScrollEvent.class, new ArrayList<Method>());
		
		methodList.put(MouseEvents.onMouseMoveEvent.class, new ArrayList<Method>());
	};
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
					Class parameterType = currentMethod.getGenericParameterTypes()[0].getClass();
					methodList.get(parameterType).add(currentMethod);
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
		superIsScrollingUp = superWheelScroll < 0 ? true : false;
		
		List eventMethodList = methodList.get(eventType.getClass());
		for(int i = 0; i < eventMethodList.size(); i++)
		{
			try
			{
				Method method = (Method)eventMethodList.get(i);
				method.invoke(((Method)eventMethodList.get(i)).getDeclaringClass() , eventType.getClass().newInstance());
			}
			catch(Exception e)
			{
				log.catching(e);
			}
		}
		
		superLastMousePosX = superMousePosX;
		superLastMousePosY = superMousePosY;
	}

	public static class onLeftClickEvent
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
	
	public static class onLeftMouseButtonPressEvent
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
	
	public static class onLeftMouseButtonReleaseEvent
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
	
	public static class onDoubleLeftClickEvent
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
	
	public static class onRightClickEvent
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
	
	public static class onRightMouseButtonPressEvent
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
	
	public static class onRightMouseButtonReleaseEvent
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
	
	public static class onDoubleRightClickEvent
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
	
	public static class onMouseButtonPressEvent
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
	
	public static class onMouseButtonReleaseEvent
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
	
	public static class onDoubleButtonClickEvent
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
	
	public static class onWheelClickEvent
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
	
	public static class onWheelPressEvent
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
	
	public static class onWheelReleaseEvent
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
	
	public static class onScrollEvent
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
	
	public static class onMouseMoveEvent
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
	
	public static class onleftClickDragEvent
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
	
	public static class onRightClickDragEvent
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