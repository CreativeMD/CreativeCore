package team.creative.creativecore.common.gui.event;

import team.creative.creativecore.common.gui.GuiControl;

public class GuiControlChangedEvent<T extends GuiControl> extends GuiControlEvent<T> {
    
    public GuiControlChangedEvent(T control) {
        super(control);
    }
    
    @Override
    public boolean cancelable() {
        return false;
    }
    
}
