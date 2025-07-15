package team.creative.creativecore.server.gui;

import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;

public class GuiServerControl<T extends GuiControl> implements GuiControlDistHandler {
    
    @Override
    public void setVisible(boolean visible) {}
    
    @Override
    public void setFixed() {}
    
    @Override
    public void setFixedX() {}
    
    @Override
    public void setFixedY() {}
    
    @Override
    public void setExpandable() {}
    
    @Override
    public void setExpandableX() {}
    
    @Override
    public void setExpandableY() {}
    
    @Override
    public void setDim(int width, int height) {}
    
    @Override
    public void setDim(GuiSizeRule dim) {}
    
    @Override
    public void setEnabled(boolean enabled) {}
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public void setTooltip(List<Component> tooltip) {}
    
    @Override
    public void setTooltip(String translate) {}
    
}
