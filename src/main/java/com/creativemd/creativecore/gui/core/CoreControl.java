package com.creativemd.creativecore.gui.core;

import com.creativemd.creativecore.gui.core.container.IControlParent;

public abstract class CoreControl {
	
	public CoreControl parent;
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
	
	//================Various Helper Methods================
	
	public boolean is(String name)
	{
		return this.name.equalsIgnoreCase(name);
	}
	
	public boolean isParentContainer()
	{
		return parent instanceof IControlParent;
	}
	
	public IControlParent getParentContainer()
	{
		if(isParentContainer())
			return (IControlParent) parent;
		return null;
	}
	
	//================Interaction================
	
	public boolean isInteractable()
	{
		return enabled;
	}
	
	//================SORTING================
	
	public void moveControlAbove(GuiControl controlInBack)
	{
		if(isParentContainer())
			getParentContainer().moveControlAbove(this, controlInBack);
	}
	
	public void moveControlBehind(GuiControl controlInFront)
	{
		if(isParentContainer())
			getParentContainer().moveControlBehind(this, controlInFront);
	}
	
	public void moveControlToBottom()
	{
		if(isParentContainer())
			getParentContainer().moveControlToBottom(this);
	}
	
	public void moveControlToTop()
	{
		if(isParentContainer())
			getParentContainer().moveControlToTop(this);
	}
	
}
