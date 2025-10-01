package team.creative.creativecore.client.gui.control;

import net.minecraft.client.input.MouseButtonInfo;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControl.GuiFocusControlDist;

public abstract class GuiFocusControl<T extends GuiControl> extends GuiClientControl<T> implements GuiFocusControlDist {
    
    public GuiFocusControl(T control) {
        super(control);
    }
    
    private boolean focused = false;
    
    @Override
    public boolean isFocused() {
        return focused;
    }
    
    @Override
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
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        focus();
        return true;
    }
    
    protected void focusChanged() {}
    
}
