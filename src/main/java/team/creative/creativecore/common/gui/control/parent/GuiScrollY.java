package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiScrollY extends GuiParent {
    
    public GuiScrollY(IGuiParent parent) {
        this(parent, "");
    }
    
    public GuiScrollY(IGuiParent parent, String name) {
        super(parent, name, GuiFlow.STACK_Y);
    }
    
    @Override
    public GuiScrollYDist dist() {
        return (GuiScrollYDist) super.dist();
    }
    
    public GuiScrollY setHovered() {
        if (dist() != null)
            dist().setHovered();
        return this;
    }
    
    public GuiScrollY setHover(boolean hover) {
        if (dist() != null)
            dist().setHover(hover);
        return this;
    }
    
    public GuiScrollY setScrollWhenCTRL() {
        if (dist() != null)
            dist().setScrollWhenCTRL();
        return this;
    }
    
    @Override
    public GuiScrollY setFormatting(ControlFormatting formatting) {
        return (GuiScrollY) super.setFormatting(formatting);
    }
    
    @Override
    public GuiScrollY setDim(int width, int height) {
        return (GuiScrollY) super.setDim(width, height);
    }
    
    @Override
    public GuiScrollY setDim(GuiSizeRule dim) {
        return (GuiScrollY) super.setDim(dim);
    }
    
    @Override
    public GuiScrollY setExpandable() {
        return (GuiScrollY) super.setExpandable();
    }
    
    public static interface GuiScrollYDist extends GuiParentDistHandler {
        
        public void setHovered();
        
        public void setHover(boolean hover);
        
        public void setScrollWhenCTRL();
        
    }
    
}
