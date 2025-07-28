package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiScrollXY extends GuiParent {
    
    public GuiScrollXY(IGuiParent parent) {
        this(parent, "");
    }
    
    public GuiScrollXY(IGuiParent parent, String name) {
        this(parent, name, GuiFlow.STACK_X);
    }
    
    public GuiScrollXY(IGuiParent parent, String name, GuiFlow flow) {
        super(parent, name, flow);
    }
    
    @Override
    public GuiScrollXYDist dist() {
        return (GuiScrollXYDist) super.dist();
    }
    
    public static interface GuiScrollXYDist extends GuiParentDistHandler {}
    
}
