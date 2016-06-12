package com.creativemd.creativecore.gui;

import com.creativemd.creativecore.gui.container.IControlParent;
import com.creativemd.creativecore.gui.event.ControlEvent;

import net.minecraft.entity.player.EntityPlayer;

public abstract class CoreControl {
	
	public IControlParent parent;
	public String name;
	
	public boolean enabled;
	
	private int id = -1;
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}
	
	public CoreControl(String name) {
		this.name = name;
		this.enabled = true;
	}
	
	//================Construction================
	
	public CoreControl setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	//================Events================
	
	public void onOpened() {}
	
	public void onClosed() {}
	
	//================Various Helper Methods================
	
	public boolean is(String name)
	{
		return this.name.equalsIgnoreCase(name);
	}
	
	public boolean hasParent()
	{
		return parent != null;
	}
	
	public EntityPlayer getPlayer()
	{
		if(hasParent())
			return parent.getPlayer();
		return null;
	}
	
	//================Internal Events================
	
	public boolean raiseEvent(ControlEvent event)
	{
		if(parent != null)
			return ((CoreControl) parent).raiseEvent(event);
		return false;
	}
	
	public void addListener(Object listener)
	{
		if(parent != null)
			((CoreControl) parent).addListener(listener);
	}
	
	public void removeListener(Object listener)
	{
		if(parent != null)
			((CoreControl) parent).removeListener(listener);
	}
	
	//================Interaction================
	
	public boolean isInteractable()
	{
		return enabled && hasParent() ? ((CoreControl) parent).isInteractable() : true;
	}
	
	//================SORTING================
	
	public void moveControlAbove(GuiControl controlInBack)
	{
		if(hasParent())
			parent.moveControlAbove(this, controlInBack);
	}
	
	public void moveControlBehind(GuiControl controlInFront)
	{
		if(hasParent())
			parent.moveControlBehind(this, controlInFront);
	}
	
	public void moveControlToBottom()
	{
		if(hasParent())
			parent.moveControlToBottom(this);
	}
	
	public void moveControlToTop()
	{
		if(hasParent())
			parent.moveControlToTop(this);
	}
	
}
