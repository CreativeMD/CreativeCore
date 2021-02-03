package team.creative.creativecore.common.gui.event;

import team.creative.creativecore.common.gui.GuiControl;

public class GuiControlChangedEvent extends GuiControlEvent {
	
	public GuiControlChangedEvent(GuiControl control) {
		super(control);
	}
	
	@Override
	public boolean cancelable() {
		return false;
	}
	
}
