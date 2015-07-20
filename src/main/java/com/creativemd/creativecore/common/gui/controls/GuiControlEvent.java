package com.creativemd.creativecore.common.gui.controls;

public abstract class GuiControlEvent {
	
	public GuiControl guiControl;
	private boolean cancelAble = isCancelAble();
	private boolean canceled = false;
	
	public GuiControlEvent(GuiControl control)
	{
		
	}
	
	public boolean isCanceled()
	{
		return canceled;
	}
	
	public void AllowIt()
	{
		canceled = false;
	}
	
	public void CancelIt()
	{
		if(cancelAble)
			canceled = true;
	}
	
	public abstract boolean isCancelAble();
	
}
