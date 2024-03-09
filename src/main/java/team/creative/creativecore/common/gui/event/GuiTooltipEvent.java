package team.creative.creativecore.common.gui.event;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiControl;

import java.util.List;

public class GuiTooltipEvent extends GuiControlEvent {
    
    public final List<Component> tooltip;
    
    public GuiTooltipEvent(GuiControl control, List<Component> tooltip) {
        super(control);
        this.tooltip = tooltip;
    }
    
    @Override
    public boolean cancelable() {
        return true;
    }
    
}
