package team.creative.creativecore.common.gui.event;

import team.creative.creativecore.common.gui.GuiControl;

public class GuiControlClickEvent extends GuiControlEvent {
    
    public final int button;
    public final boolean doubleClick;
    
    public GuiControlClickEvent(GuiControl control, int button, boolean doubleClick) {
        super(control);
        this.button = button;
        this.doubleClick = doubleClick;
    }
    
    @Override
    public boolean cancelable() {
        return false;
    }
    
}
