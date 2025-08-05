package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Consumer;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.util.type.Color;

public class GuiCheckButtonIcon extends GuiButtonIcon {
    
    public GuiCheckButtonIcon(IGuiParent parent, String name, Icon on, Icon off, boolean state) {
        this(parent, name, on, off, state, null);
    }
    
    public GuiCheckButtonIcon(IGuiParent parent, String name, Icon on, Icon off, boolean state, Consumer<Integer> pressed) {
        super(parent, name, state ? on : off, pressed);
        if (dist() != null) {
            dist().setState(state);
            dist().setOnIcon(on);
            dist().setOffIcon(off);
        }
    }
    
    @Override
    public GuiCheckButtonIconDist dist() {
        return (GuiCheckButtonIconDist) super.dist();
    }
    
    public GuiCheckButtonIcon setOnIcon(Icon icon) {
        if (dist() != null)
            dist().setOnIcon(icon);
        return this;
    }
    
    public GuiCheckButtonIcon setOffIcon(Icon icon) {
        if (dist() != null)
            dist().setOffIcon(icon);
        return this;
    }
    
    @Override
    public GuiCheckButtonIcon setColor(Color color) {
        return (GuiCheckButtonIcon) super.setColor(color);
    }
    
    @Override
    public GuiCheckButtonIcon setShadow(Color shadow) {
        return (GuiCheckButtonIcon) super.setShadow(shadow);
    }
    
    @Override
    public GuiCheckButtonIcon setSquared(boolean squared) {
        return (GuiCheckButtonIcon) super.setSquared(squared);
    }
    
    @Override
    public GuiCheckButtonIcon setDim(GuiSizeRule dim) {
        super.setDim(dim);
        return this;
    }
    
    @Override
    public GuiCheckButtonIcon setDim(int width, int height) {
        super.setDim(width, height);
        return this;
    }
    
    public boolean getState() {
        if (dist() != null)
            return dist().getState();
        return false;
    }
    
    public void setState(boolean value) {
        if (dist() != null)
            if (dist().getState() != value) {
                dist().setState(value);
                this.raiseEvent(new GuiControlChangedEvent<>(this));
            }
    }
    
    public static interface GuiCheckButtonIconDist extends GuiButtonIconDist {
        
        public void setOnIcon(Icon icon);
        
        public void setOffIcon(Icon icon);
        
        public boolean getState();
        
        public void setState(boolean value);
    }
}