package team.creative.creativecore.client.gui.control;

import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.common.gui.GuiControl;

public abstract class GuiFocusControl<T extends GuiControl> extends GuiClientControl<T> {
    
    public GuiFocusControl(T control) {
        super(control);
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
