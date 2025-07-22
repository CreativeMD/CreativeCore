package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiPanel extends GuiParent {
    
    public GuiPanel(IGuiParent parent, String name, GuiFlow flow) {
        super(parent, name, flow);
    }
    
    public GuiPanel(IGuiParent parent, String name, GuiFlow flow, VAlign valign) {
        super(parent, name, flow, valign);
    }
    
    public GuiPanel(IGuiParent parent, String name, GuiFlow flow, Align align, VAlign valign) {
        super(parent, name, flow, align, valign);
    }
    
    public GuiPanel(IGuiParent parent, String name) {
        super(parent, name);
    }
    
    public GuiPanel(IGuiParent parent) {
        super(parent);
    }
    
    public GuiPanel(IGuiParent parent, GuiFlow flow) {
        super(parent, flow);
    }
    
}
