package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiTopBottomBox extends GuiParent {
    
    public final GuiParent top;
    public final GuiParent bottom;
    
    public GuiTopBottomBox() {
        super(GuiFlow.STACK_Y);
        this.spacing = 0;
        setExpandableY();
        align = Align.STRETCH;
        valign = VAlign.STRETCH;
        this.top = (GuiParent) new GuiParent(GuiFlow.STACK_Y).setExpandableY();
        super.addControl(top);
        this.bottom = new GuiParent(GuiFlow.STACK_Y);
        super.addControl(bottom);
    }
    
    public GuiTopBottomBox addTop(GuiControl control) {
        top.add(control);
        return this;
    }
    
    public GuiTopBottomBox addBottom(GuiControl control) {
        bottom.add(control);
        return this;
    }
    
    @Override
    @Deprecated
    public GuiChildControl addControl(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
}
