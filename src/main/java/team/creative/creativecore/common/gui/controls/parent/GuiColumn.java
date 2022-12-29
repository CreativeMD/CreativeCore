package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.flow.GuiSizeRule.GuiSizeRuleCustom;

public class GuiColumn extends GuiParent {
    
    public GuiColumn() {
        super(GuiFlow.STACK_X);
    }
    
    public GuiColumn(GuiFlow flow) {
        super(flow);
    }
    
    public GuiColumn(int width) {
        this(width, GuiFlow.STACK_X);
        
    }
    
    public GuiColumn(int width, GuiFlow flow) {
        super(flow);
        setDim(new GuiSizeRuleCustom().prefWidth(width));
    }
    
}
