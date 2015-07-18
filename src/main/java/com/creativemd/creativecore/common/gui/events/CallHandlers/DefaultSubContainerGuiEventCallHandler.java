package com.creativemd.creativecore.common.gui.events.CallHandlers;

import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.events.GuiEventHandler;
import com.creativemd.creativecore.common.gui.events.KeyBoardEvents;
import com.creativemd.creativecore.common.gui.events.MouseEvents;
import com.creativemd.creativecore.core.CreativeCore;

public class DefaultSubContainerGuiEventCallHandler extends GuiEventCallHandler
{
	private static final Logger log = CreativeCore.logger;
	private GuiContainerSub mainInstance;

	@Override
	public void addSingleGuiControlListner(Object guiContainerSub)
	{
		if(!(guiContainerSub instanceof GuiContainerSub))
			log.catching(new IllegalArgumentException("Current object isn't an instance of GuiContainerSub"));
		mainInstance = (GuiContainerSub) guiContainerSub;
	}

	@Override
	public void callEvents(Class<? extends GuiEventHandler.DummyEventClass> eventType)
	{
		if(mainInstance.gui != null)
		{
			// you can do some magic here for the subGui. it should be changed to something like 'focesedSubGui'instead though.
			//mainInstance.gui.onControlEvent(control, event);
		}
		
		 /* the current available methods aren't matching the methods from the MouseEvents Class, though instead overwriting the current available eventMethods from Vanilla.
		  * By adding/changing methods, these methodCalls will be made. any Parameter you need is available through the EventType Class.
		if (eventType == MouseEvents.onMouseButtonPressEvent.class)
			mainInstance.onMouseButtonPress(new MouseEvents.onMouseButtonPressEvent());
		if (eventType == MouseEvents.onMouseButtonReleaseEvent.class)
			mainInstance.onMouseButtonReleasedEvent(new MouseEvents.onMouseButtonReleaseEvent());
		if (eventType == MouseEvents.onMouseMoveEvent.class)
			mainInstance.onMouseMove(new MouseEvents.onMouseMoveEvent());
		if (eventType == MouseEvents.onMouseButtonPressDragEvent.class)
			mainInstance.onMouseButtonPressDragEvent(new MouseEvents.onMouseButtonPressDragEvent());
		if(eventType == MouseEvents.onDoubleButtonClickEvent.class)
			mainInstance.onDoubleMouseButtonClick(new MouseEvents.onDoubleButtonClickEvent());
		if (eventType == MouseEvents.onScrollEvent.class)
			mainInstance.onMouseScroll(new MouseEvents.onScrollEvent());
		if (eventType == KeyBoardEvents.onKeyPress.class)
			mainInstance.onKeyPress(new keyboardEvents.onKeyPress());
		if (eventType == KeyBoardEvents.onDoubleKeyPress.class)
			mainInstance.onDoubleKeyPress(new KeyboardEvents.onDoubleKeyPress());	
		*/
			
	}

}
