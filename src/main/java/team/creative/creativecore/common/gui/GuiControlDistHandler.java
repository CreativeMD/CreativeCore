package team.creative.creativecore.common.gui;

import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public interface GuiControlDistHandler {
    
    public void setVisible(boolean visible);
    
    public void setFixed();
    
    public void setFixedX();
    
    public void setFixedY();
    
    public void setExpandable();
    
    public void setExpandableX();
    
    public void setExpandableY();
    
    public boolean isExpandableX();
    
    public boolean isExpandableY();
    
    public void setDim(int width, int height);
    
    public void setDim(GuiSizeRule dim);
    
    public void setEnabled(boolean enabled);
    
    public void setTooltip(List<Component> tooltip);
    
    public void setTooltip(String translate);
    
    public void removeFormatting();
    
    public void setFormatting(ControlFormatting formatting);
    
}
