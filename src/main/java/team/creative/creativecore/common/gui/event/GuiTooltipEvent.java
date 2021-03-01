package team.creative.creativecore.common.gui.event;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import team.creative.creativecore.common.gui.GuiControl;

public class GuiTooltipEvent extends GuiControlEvent {
    
    public List<ITextComponent> tooltip;
    
    public GuiTooltipEvent(GuiControl control, List<ITextComponent> tooltip) {
        super(control);
        this.tooltip = tooltip;
    }
    
    @Override
    public boolean cancelable() {
        return true;
    }
    
}
