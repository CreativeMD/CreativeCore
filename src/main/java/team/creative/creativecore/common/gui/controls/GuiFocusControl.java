package team.creative.creativecore.common.gui.controls;

public abstract class GuiFocusControl extends GuiControlBasic {
    
    public GuiFocusControl(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
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
