package team.creative.creativecore.common.gui.control.simple;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.util.type.Color;

public class GuiStateButtonIcon extends GuiButtonIcon {
    
    public GuiStateButtonIcon(IGuiParent parent, String name, Icon... icons) {
        super(parent, name, icons[0], null);
        if (dist() != null)
            dist().setIcons(false, icons);
    }
    
    public GuiStateButtonIcon setState(int index) {
        if (dist() != null)
            dist().setState(index, true);
        return this;
    }
    
    @Override
    public GuiStateButtonIconDist dist() {
        return (GuiStateButtonIconDist) super.dist();
    }
    
    public int getState() {
        if (dist() != null)
            return dist().getState();
        return 0;
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
        if (dist() != null)
            dist().previousState();
    }
    
    public void nextState() {
        if (dist() != null)
            dist().nextState();
    }
    
    public static interface GuiStateButtonIconDist extends GuiButtonIconDist {
        
        public int getState();
        
        public void setState(int index, boolean notify);
        
        public void setIcons(boolean notify, Icon... icons);
        
        public void previousState();
        
        public void nextState();
        
    }
}
