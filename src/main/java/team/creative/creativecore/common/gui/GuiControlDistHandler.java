package team.creative.creativecore.common.gui;

import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;

public interface GuiControlDistHandler {
    
    public void init();
    
    public void closed();
    
    public void tick();
    
    public void setVisible(boolean visible);
    
    public void setFixed();
    
    public void setFixedX();
    
    public void setFixedY();
    
    public void setExpandable();
    
    public void setExpandableX();
    
    public void setExpandableY();
    
    public void setDim(int width, int height);
    
    public void setDim(GuiSizeRule dim);
    
    public void setEnabled(boolean enabled);
    
    public void setTooltip(List<Component> tooltip);
    
    public void setTooltip(String translate);
    
}
