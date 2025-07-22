package team.creative.creativecore.client.gui.control.parent;

import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.common.gui.control.parent.GuiPanel;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientPanel<T extends GuiPanel> extends GuiClientParent<T> {
    
    public GuiClientPanel(T control) {
        super(control);
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.NESTED;
    }
    
}
