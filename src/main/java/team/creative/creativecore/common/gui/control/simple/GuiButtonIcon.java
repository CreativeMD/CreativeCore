package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Consumer;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.util.type.Color;

public class GuiButtonIcon extends GuiIcon {
    
    public GuiButtonIcon(IGuiParent parent, String name, Icon icon, Consumer<Integer> pressed) {
        super(parent, name, icon);
        dist().setPressed(pressed);
    }
    
    @Override
    public GuiButtonIconDist dist() {
        return (GuiButtonIconDist) super.dist();
    }
    
    @Override
    public GuiButtonIcon setIcon(Icon icon) {
        super.setIcon(icon);
        return this;
    }
    
    @Override
    public GuiButtonIcon setColor(Color color) {
        super.setColor(color);
        return this;
    }
    
    @Override
    public GuiButtonIcon setShadow(Color colorShadow) {
        super.setShadow(colorShadow);
        return this;
    }
    
    @Override
    public GuiButtonIcon setSquared(boolean squared) {
        super.setSquared(squared);
        return this;
    }
    
    public static interface GuiButtonIconDist extends GuiIconDist {
        
        public void setPressed(Consumer<Integer> pressed);
        
    }
    
}
