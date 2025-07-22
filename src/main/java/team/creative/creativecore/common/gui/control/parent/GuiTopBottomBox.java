package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiTopBottomBox extends GuiParent {
    
    public final GuiParent top;
    public final GuiParent bottom;
    
    public GuiTopBottomBox(IGuiParent parent) {
        super(parent, GuiFlow.STACK_Y);
        setSpacing(0);
        setExpandableY();
        setAlign(Align.STRETCH);
        setVAlign(VAlign.STRETCH);
        this.top = (GuiParent) new GuiParent(parent, GuiFlow.STACK_Y).setExpandableY();
        super.add(top);
        this.bottom = new GuiParent(parent, GuiFlow.STACK_Y);
        super.add(bottom);
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
    public GuiParent add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Deprecated
    public GuiParent addHover(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
}
