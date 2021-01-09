package com.creativemd.creativecore.common.gui.event.container;

import com.creativemd.creativecore.common.gui.ContainerControl;
import com.creativemd.creativecore.common.gui.event.ControlEvent;

public abstract class ContainerControlEvent extends ControlEvent {
    
    public ContainerControlEvent(ContainerControl source) {
        super(source);
    }
    
}
