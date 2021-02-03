package team.creative.creativecore.common.gui.event;

import team.creative.creativecore.common.gui.GuiControl;

public abstract class GuiControlEvent extends GuiEvent {
	
	public final GuiControl control;
	
	public GuiControlEvent(GuiControl control) {
		this.control = control;
	}
	
}
