package com.creativemd.creativecore.gui.event.container;

import com.creativemd.creativecore.gui.ContainerControl;

public class SlotChangeEvent extends ContainerControlEvent {
	
	public SlotChangeEvent(ContainerControl source) {
		super(source);
	}
	
	@Override
	public boolean isCancelable() {
		return false;
	}
	
}
