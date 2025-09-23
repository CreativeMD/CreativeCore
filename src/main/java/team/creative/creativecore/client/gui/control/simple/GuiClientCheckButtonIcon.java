package team.creative.creativecore.client.gui.control.simple;

import team.creative.creativecore.common.gui.control.simple.GuiCheckButtonIcon;
import team.creative.creativecore.common.gui.control.simple.GuiCheckButtonIcon.GuiCheckButtonIconDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.Icon;

public class GuiClientCheckButtonIcon<T extends GuiCheckButtonIcon> extends GuiClientButtonIcon<T> implements GuiCheckButtonIconDist {
    
    protected Icon on, off;
    public boolean value;
    
    public GuiClientCheckButtonIcon(T control) {
        super(control);
    }
    
    @Override
    public void setOnIcon(Icon icon) {
        this.on = icon;
    }
    
    @Override
    public void setOffIcon(Icon icon) {
        this.off = icon;
    }
    
    @Override
    public boolean getState() {
        return value;
    }
    
    @Override
    public void setState(boolean value, boolean notify) {
        this.value = value;
        setIcon(value ? on : off);
        if (notify)
            raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        this.setState(!this.value, true);
        return super.mouseClicked(x, y, button);
    }
    
}
