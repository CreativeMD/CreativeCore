package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiColumn extends GuiParent {
    
    public GuiColumn() {
        super(GuiFlow.STACK_X);
    }
    
    public GuiColumn(int width) {
        super(GuiFlow.STACK_X);
        this.preferredWidth = width;
    }
    
    @Override
    public int getPreferredWidth() {
        if (preferredWidth != 0)
            return preferredWidth;
        return super.getPreferredWidth();
    }
    
}
