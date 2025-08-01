package team.creative.creativecore.common.gui.control.simple;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.util.type.Color;

public class GuiIcon extends GuiControl {
    
    public GuiIcon(IGuiParent parent, String name, Icon icon) {
        super(parent, name);
        dist().setIcon(icon);
    }
    
    @Override
    public GuiIconDist dist() {
        return (GuiIconDist) super.dist();
    }
    
    public GuiIcon setIcon(Icon icon) {
        dist().setIcon(icon);
        return this;
    }
    
    public GuiIcon setColor(Color color) {
        dist().setColor(color);
        return this;
    }
    
    public GuiIcon setShadow(Color shadowColor) {
        dist().setShadow(shadowColor);
        return this;
    }
    
    public GuiIcon setSquared(boolean squared) {
        dist().setSquared(squared);
        return this;
    }
    
    @Override
    public GuiIcon setDim(int width, int height) {
        super.setDim(width, height);
        return this;
    }
    
    @Override
    public GuiIcon setDim(GuiSizeRule dim) {
        super.setDim(dim);
        return this;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    public static interface GuiIconDist extends GuiControlDistHandler {
        
        public void setIcon(Icon icon);
        
        public void setColor(Color color);
        
        public void setShadow(Color shadowColor);
        
        public void setSquared(boolean squared);
        
    }
    
}
