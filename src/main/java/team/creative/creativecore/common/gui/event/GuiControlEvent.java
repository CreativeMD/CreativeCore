package team.creative.creativecore.common.gui.event;

import team.creative.creativecore.common.gui.GuiControl;

public abstract class GuiControlEvent<T extends GuiControl> extends GuiEvent {
    
    public final T control;
    
    public GuiControlEvent(T control) {
        this.control = control;
    }
    
}
