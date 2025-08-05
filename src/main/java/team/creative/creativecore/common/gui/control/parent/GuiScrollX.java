package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;

public class GuiScrollX extends GuiParent {
    
    public GuiScrollX(IGuiParent parent) {
        this(parent, "");
    }
    
    public GuiScrollX(IGuiParent parent, String name) {
        super(parent, name, GuiFlow.STACK_X);
    }
    
    @Override
    public GuiScrollXDist dist() {
        return (GuiScrollXDist) super.dist();
    }
    
    public GuiScrollX setHovered() {
        if (dist() != null)
            dist().setHovered();
        return this;
    }
    
    public GuiScrollX setHover(boolean hover) {
        if (dist() != null)
            dist().setHover(hover);
        return this;
    }
    
    @Override
    public GuiScrollX setDim(int width, int height) {
        return (GuiScrollX) super.setDim(width, height);
    }
    
    @Override
    public GuiScrollX setDim(GuiSizeRule dim) {
        return (GuiScrollX) super.setDim(dim);
    }
    
    @Override
    public GuiScrollX setExpandable() {
        return (GuiScrollX) super.setExpandable();
    }
    
    public static interface GuiScrollXDist extends GuiParentDistHandler {
        
        public void setHovered();
        
        public void setHover(boolean hover);
    }
}
