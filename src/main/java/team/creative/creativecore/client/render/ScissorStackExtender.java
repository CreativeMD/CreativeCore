package team.creative.creativecore.client.render;

import javax.annotation.Nullable;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface ScissorStackExtender {
    
    public void setOverrideScissor(@Nullable ScreenRectangle rect);
    
    public void clearOverrideScissor();
    
}
