package team.creative.creativecore.common.gui.event;

import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiControl;

public class GuiTooltipEvent extends GuiControlEvent {
    
    public final List<Component> tooltip;
    public final boolean extraSpaceAfterFirstLine;
    
    public GuiTooltipEvent(GuiControl control, List<Component> tooltip, boolean extraSpaceAfterFirstLine) {
        super(control);
        this.tooltip = tooltip;
        this.extraSpaceAfterFirstLine = extraSpaceAfterFirstLine;
    }
    
    @Override
    public boolean cancelable() {
        return true;
    }
    
}
