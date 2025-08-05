package team.creative.creativecore.server.gui;

import java.util.List;

import net.minecraft.core.Holder.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormattingFlexible;
import team.creative.creativecore.common.gui.style.ControlFormattingFlexible.ControlFormattingFlexibleEmpty;

public class GuiServerControl<T extends GuiControl> implements GuiControlDistHandler {
    
    public final T control;
    
    public GuiServerControl(T control) {
        this.control = control;
    }
    
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
    public void setTooltip(List<Component> tooltip) {}
    
    @Override
    public void setTooltip(String translate) {}
    
    @Override
    public boolean isExpandableX() {
        return false;
    }
    
    @Override
    public boolean isExpandableY() {
        return false;
    }
    
    @Override
    public void removeFormatting() {}
    
    @Override
    public void setFormatting(ControlFormatting formatting) {}
    
    @Override
    public ControlFormattingFlexible setCustomFormatting() {
        return new ControlFormattingFlexibleEmpty();
    }
    
    @Override
    public void playSound(Reference<SoundEvent> sound) {}
    
    @Override
    public void playSound(SoundEvent event) {}
    
    @Override
    public void playSound(SoundEvent event, float volume, float pitch) {}
    
    @Override
    public void playSound(Reference<SoundEvent> event, float volume, float pitch) {}
    
}
