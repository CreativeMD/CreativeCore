package team.creative.creativecore.common.gui.controls;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.util.math.geo.Rect;

public abstract class GuiFocusControl extends GuiControl {
    
    public GuiFocusControl(String name) {
        super(name);
    }
    
    public GuiFocusControl(String name, int width, int height) {
        super(name, width, height);
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
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        focus();
        return true;
    }
    
    protected void focusChanged() {}
    
}
