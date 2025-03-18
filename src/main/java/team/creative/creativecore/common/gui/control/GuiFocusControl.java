package team.creative.creativecore.common.gui.control;

import team.creative.creativecore.common.gui.GuiControl;

public abstract class GuiFocusControl extends GuiControl {
    
    public GuiFocusControl(String name) {
        super(name);
    }
    
    private boolean focused = false;
    
    public boolean isFocused() {
        return focused;
    }
    
    public void focus() {
        focused = true;
        focusChanged();
    }
    
    @Override
    public void looseFocus() {
        focused = false;
        focusChanged();
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        focus();
        return true;
    }
    
    protected void focusChanged() {}
    
}
