package com.creativemd.creativecore.common.gui.events.CallHandlers;

import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.gui.events.GuiEventHandler;

public abstract class GuiEventCallHandler
{
	public abstract void addSingleGuiControlListner(Object Listner);
	
	public abstract void callEvents(Class<? extends GuiEventHandler.DummyEventClass> eventType);
	
}