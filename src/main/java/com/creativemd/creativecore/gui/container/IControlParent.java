package com.creativemd.creativecore.gui.container;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.gui.CoreControl;
import com.creativemd.creativecore.gui.event.ControlEvent;
import net.minecraft.entity.player.EntityPlayer;

public interface IControlParent {
	
	public List getControls();
	
	public void refreshControls();
	
	public CoreControl get(String name);
	
	public boolean has(String name);
	
	public EntityPlayer getPlayer();
	
	public default void moveControlBehind(CoreControl control, CoreControl controlInBack)
	{
		List controls = getControls();
		if(controls.contains(controlInBack) && controls.remove(control) && controls.indexOf(controlInBack)+1 < controls.size())
			controls.add(controls.indexOf(controlInBack)+1, control);
		else
			moveControlToBottom(control);		
		refreshControls();
	}
	
	public default void moveControlAbove(CoreControl control, CoreControl controlInFront)
	{
		List controls = getControls();
		if(controls.contains(controlInFront) && controls.remove(control))
			controls.add(controls.indexOf(controlInFront), control);
		refreshControls();
	}
	
	public default void moveControlToTop(CoreControl control)
	{
		List controls = getControls();
		if(controls.remove(control))
			controls.add(1, control);
		refreshControls();
	}
	
	public default void moveControlToBottom(CoreControl control)
	{
		List controls = getControls();
		if(controls.remove(control))
			controls.add(control);
		refreshControls();
	}
}
