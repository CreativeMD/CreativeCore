package com.creativemd.creativecore.gui.event.container;

import com.creativemd.creativecore.gui.ContainerControl;
import com.creativemd.creativecore.gui.event.ControlEvent;

public abstract class ContainerControlEvent extends ControlEvent {

	public ContainerControlEvent(ContainerControl source) {
		super(source);
	}

}
