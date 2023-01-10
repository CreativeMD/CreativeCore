package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiPanel extends GuiParent {
    
    public GuiPanel(String name, GuiFlow flow) {
        super(name, flow);
    }
    
    public GuiPanel(String name, GuiFlow flow, VAlign valign) {
        super(name, flow, valign);
    }
    
    public GuiPanel(String name, GuiFlow flow, Align align, VAlign valign) {
        super(name, flow, align, valign);
    }
    
    public GuiPanel(String name) {
        super(name);
    }
    
    public GuiPanel() {
        super();
    }
    
    public GuiPanel(GuiFlow flow) {
        super(flow);
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.NESTED;
    }
}
