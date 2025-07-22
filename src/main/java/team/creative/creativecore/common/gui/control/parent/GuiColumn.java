package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.flow.GuiSizeRule.GuiSizeRules;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiColumn extends GuiParent {
    
    public GuiColumn(IGuiParent parent) {
        super(parent, GuiFlow.STACK_X);
    }
    
    public GuiColumn(IGuiParent parent, GuiFlow flow) {
        super(parent, flow);
    }
    
    public GuiColumn(IGuiParent parent, int width) {
        this(parent, width, GuiFlow.STACK_X);
        
    }
    
    public GuiColumn(IGuiParent parent, int width, GuiFlow flow) {
        super(parent, flow);
        setDim(new GuiSizeRules().prefWidth(width));
    }
    
    public static class GuiColumnHeader extends GuiColumn {
        
        public GuiColumnHeader(IGuiParent parent) {
            super(parent);
            setFormatting(ControlFormatting.HEADER);
        }
        
        public GuiColumnHeader(IGuiParent parent, GuiFlow flow) {
            super(parent, flow);
        }
        
        public GuiColumnHeader(IGuiParent parent, int width) {
            super(parent, width);
        }
        
        public GuiColumnHeader(IGuiParent parent, int width, GuiFlow flow) {
            super(parent, width, flow);
        }
        
    }
    
}
