package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.flow.GuiSizeRule.GuiSizeRules;
import team.creative.creativecore.common.gui.style.ControlFormatting;

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
        setDim(new GuiSizeRules().prefWidth(width));
    }
    
    public static class GuiColumnHeader extends GuiColumn {
        
        public GuiColumnHeader() {
            super();
        }
        
        public GuiColumnHeader(GuiFlow flow) {
            super(flow);
        }
        
        public GuiColumnHeader(int width) {
            super(width);
        }
        
        public GuiColumnHeader(int width, GuiFlow flow) {
            super(width, flow);
        }
        
        @Override
        public ControlFormatting getControlFormatting() {
            return ControlFormatting.HEADER;
        }
    }
    
}
