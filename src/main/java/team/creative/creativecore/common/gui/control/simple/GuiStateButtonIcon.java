package team.creative.creativecore.common.gui.control.simple;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.util.type.Color;

public class GuiStateButtonIcon extends GuiButtonIcon {
    
    public GuiStateButtonIcon(IGuiParent parent, String name, Icon... icons) {
        super(parent, name, icons[0], null);
        dist().setIcons(icons);
    }
    
    public GuiStateButtonIcon setState(int index) {
        dist().setState(index);
        return this;
    }
    
    @Override
    public GuiStateButtonIconDist dist() {
        return (GuiStateButtonIconDist) super.dist();
    }
    
    public int getState() {
        return dist().getState();
    }
    
    @Override
    public GuiStateButtonIcon setShadow(Color shadow) {
        return (GuiStateButtonIcon) super.setShadow(shadow);
    }
    
    @Override
    public GuiStateButtonIcon setColor(Color color) {
        return (GuiStateButtonIcon) super.setColor(color);
    }
    
    public void previousState() {
        dist().previousState();
    }
    
    public void nextState() {
        dist().nextState();
    }
    
    public static interface GuiStateButtonIconDist extends GuiButtonIconDist {
        
        public int getState();
        
        public void setState(int index);
        
        public void setIcons(Icon... icons);
        
        public void previousState();
        
        public void nextState();
        
    }
}
