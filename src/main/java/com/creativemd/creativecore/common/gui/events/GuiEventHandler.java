package com.creativemd.creativecore.common.gui.events;

import java.awt.Toolkit;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEventHandler
{
	// ////////////////////////////
	/*
	 * MouseEvents
	 */
	// ////////////////////////////
	public static final Integer timerInterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval") / 50;
	public static boolean EnableMouseHandling;
	private static boolean isMoving;
	private static boolean[] mouseButtonReleased = new boolean[9];
	private static boolean[] Clicked = new boolean[9];
	private static int[] mouseButtonTimer = new int[9];
	private static Mouse mouse;
	private static int mouseX;
	private static int mouseY;

	public static void handleMouseEvent()
	{
		if (EnableMouseHandling)
		{
			if (mouseX != mouse.getEventX() || mouseY != mouse.getEventY())
			{
				MouseEvents.onMouseMoveEvent.callActionEvents();
				isMoving = true;
			}
			else isMoving = false;
			
			int dMouseX = mouse.getEventX() - mouseX;
			int dMouseY = mouse.getEventY() - mouseY;
			boolean isSignificantMoving = (dMouseX > 3 || dMouseY > 3);
			mouseX = mouse.getEventX();
			mouseY = mouse.getEventY();

			int wheel = mouse.getEventDWheel();
			if (wheel != 0)
				MouseEvents.onScrollEvent.callActionEvents(wheel);

			int i = mouse.getEventButton();
			
			if (mouse.isButtonDown(i))
			{
				if (mouseButtonReleased[i])
				{
					switch (i)
					{
					case 0:
						MouseEvents.onLeftMouseButtonPressEvent.callActionEvents();
						break;
					case 1:
						MouseEvents.onRightMouseButtonPressEvent.callActionEvents();
						break;
					case 2:
						MouseEvents.onWheelPressEvent.callActionEvents();
					}
					MouseEvents.onMouseButtonPressEvent.callActionEvents(i);
					if (mouseButtonTimer[i] == -1)
						mouseButtonTimer[i] = 0;
					mouseButtonReleased[i] = false;
				}
				
				if(isSignificantMoving)
				{
					if(i == 0)
						MouseEvents.onRightClickDragEvent.callActionEvents();
					else if(i == 1)
						MouseEvents.onleftClickDragEvent.callActionEvents();
					MouseEvents.onMouseButtonPressDragEvent.callActionEvents();
				}
				
				if (mouseButtonTimer[i] != -1)
					mouseButtonTimer[i] += 1;
				if (mouseButtonTimer[i] > timerInterval)
					mouseButtonTimer[i] = -1;
			}
			else
			{
				if (!mouseButtonReleased[i])
				{
					if (mouseButtonTimer[i] < timerInterval)
					{
						if (Clicked[i] && !isSignificantMoving)
						{
							if (i == 0)
								MouseEvents.onDoubleLeftClickEvent.callActionEvents();
							else if (i == 1)
								MouseEvents.onDoubleRightClickEvent.callActionEvents();
							MouseEvents.onDoubleButtonClickEvent.callActionEvents(i);
							Clicked[i] = false;
						}
						else Clicked[i] = true;
					}

					switch (i)
					{
					case 0:
						MouseEvents.onRightClickEvent.callActionEvents();
						MouseEvents.onRightMouseButtonReleaseEvent.callActionEvents();
						break;
					case 1:
						MouseEvents.onLeftClickEvent.callActionEvents();
						MouseEvents.onLeftMouseButtonReleaseEvent.callActionEvents();
						break;
					case 2:
						MouseEvents.onWheelClickEvent.callActionEvents();
					}
					MouseEvents.onMouseButtonReleaseEvent.callActionEvents(i);
				}
				if (mouseButtonTimer[i] > timerInterval)
					mouseButtonTimer[i] = -1;
			}
		}
	}

	// ////////////////////////////
	/*
	 * KeyboardEvents
	 */
	// ////////////////////////////
	public static boolean enableKeyboardHandling;
	private static Keyboard keyboard;
	private static boolean[] isKeyReleased = new boolean[keyboard.getKeyCount()];
	private static boolean[] keyPressed = new boolean[keyboard.getKeyCount()];
	private static int[] keyTimer = new int[keyboard.getKeyCount()];
	private static int keyboardCount = 0;

	public static void handleKeyboardEvents()
	{
		if (enableKeyboardHandling)
		{
			if (keyboardCount != keyboard.getKeyCount())
			{
				keyboardCount = keyboard.getKeyCount();
				isKeyReleased = new boolean[keyboard.getKeyCount()];
				keyPressed = new boolean[keyboard.getKeyCount()];
				keyTimer = new int[keyboard.getKeyCount()];
			}
			int i = keyboard.getEventKey();
			{
				if (keyboard.getEventKeyState())
				{
					if (isKeyReleased[i])
					{
						KeyBoardEvents.onKeyPress.callActionEvents(i);
						
						if (keyTimer[i] == -1)
							keyTimer[i] = 0;
						isKeyReleased[i] = false;
					}
					
					if (keyTimer[i] != -1)
						keyTimer[i] += 1;
					if (keyTimer[i] > timerInterval)
						keyTimer[i] = -1;
				}
				else
				{
					if (!isKeyReleased[i])
					{
						if(keyTimer[i] < timerInterval)
						{
							if(keyPressed[i])
							{
								KeyBoardEvents.onDoubleKeyPress.callActionEvents(i);
							}
						}
						KeyBoardEvents.onKeyRelease.callActionEvents(i);
						isKeyReleased[i] = true;
					}
					if (keyTimer[i] > timerInterval)
						keyTimer[i] = -1;
				}
			}
		}
	}

	/**used to Check whether an parameterType is an EventType */
	public static class DummyEventClass{}
}
