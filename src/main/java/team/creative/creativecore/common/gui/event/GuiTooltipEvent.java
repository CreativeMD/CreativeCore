package team.creative.creativecore.common.gui.event;

import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiControl;

public class GuiTooltipEvent extends GuiControlEvent {
    
    public List<Component> tooltip;
    
    public GuiTooltipEvent(GuiControl control, List<Component> tooltip) {
        super(control);
        this.tooltip = tooltip;
    }
    
    @Override
    public boolean cancelable() {
        return true;
    }
    
}
