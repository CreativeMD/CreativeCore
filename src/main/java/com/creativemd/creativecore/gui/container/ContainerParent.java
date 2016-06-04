package com.creativemd.creativecore.gui.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.creativemd.creativecore.gui.ContainerControl;
import com.creativemd.creativecore.gui.CoreControl;
import com.creativemd.creativecore.gui.GuiControl;

import net.minecraft.nbt.NBTTagCompound;

public abstract class ContainerParent extends ContainerControl implements IControlParent {

	public ContainerParent(String name) {
		super(name);
	}
	
	public ArrayList<ContainerControl> controls = new ArrayList<>();
	
	@Override
	public List getControls() {
		return controls;
	}
	
	@Override
	public CoreControl get(String name)
    {
    	for (int i = 0; i < controls.size(); i++) {
			if(controls.get(i).name.equalsIgnoreCase(name))
				return controls.get(i);
		}
    	return null;
    }
    
	@Override
    public boolean has(String name)
    {
    	for (int i = 0; i < controls.size(); i++) {
			if(controls.get(i).name.equalsIgnoreCase(name))
				return true;
		}
    	return false;
    }
	
	@Override
	public void onOpened()
    {
    	for (int i = 0; i < controls.size(); i++) {
    		controls.get(i).parent = this;
    		controls.get(i).onOpened();
    	}
		refreshControls();
    }
	
	@Override
	public void onClosed()
	{
		for (int i = 0; i < controls.size(); i++) {
			controls.get(i).onClosed();
		}
		//eventBus.removeAllEventListeners();
	}
	
	public void updateEqualContainers()
	{
		if(parent != null)
			getParent().updateEqualContainers();
	}
	
	@Override
	public void refreshControls() {
		for (int i = 0; i < controls.size(); i++)
		{
			controls.get(i).parent = this;
			controls.get(i).setID(i);
		}
	}

	public void sendNBTUpdate(ContainerControl control, NBTTagCompound nbt)
	{
		if(parent != null)
			getParent().sendNBTUpdate(control, nbt);
	}

}
