package com.creativemd.creativecore.common.gui.premade;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import net.minecraft.client.gui.FontRenderer;

import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.gui.event.GuiControlEvent;
import com.n247s.api.eventapi.eventsystem.CallHandler;
import com.n247s.api.eventapi.eventsystem.EventApiCallHandler;
import com.n247s.api.eventapi.eventsystem.EventType;

public class SubGuiControl extends SubGui{
	
	public SubGuiControl(GuiControl parent)
	{
		this.parent = parent;
	}
	
	public GuiControl parent;
	
	@Override
	public boolean raiseEvent(GuiControlEvent event)
	{
		return parent.raiseEvent(event);
	}
	
	@Override
	public Vector2d getMousePos()
	{
		Vector2d mouse = parent.parent.getMousePos();
		mouse.x -= parent.posX-parent.width/2;
		mouse.y -= parent.posY-parent.height/2;
		return mouse;
	}
	
	@Override
	public void drawOverlay(FontRenderer fontRenderer) {
		
	}


	@Override
	public void createControls() {
		
	}

}
