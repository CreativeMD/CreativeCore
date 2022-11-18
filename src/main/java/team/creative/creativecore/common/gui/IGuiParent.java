package team.creative.creativecore.common.gui;

import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.packet.LayerOpenPacket;
import team.creative.creativecore.common.util.math.geo.Rect;

public interface IGuiParent {
    
    public boolean isContainer();
    
    public boolean isClient();
    
    public Player getPlayer();
    
    public GuiLayer openLayer(LayerOpenPacket packet);
    
    public void closeTopLayer();
    
    public void closeLayer(GuiLayer layer);
    
    public void raiseEvent(GuiEvent event);
    
    public void reflow();
    
    public boolean hasGui();
    
    public boolean isParent(IGuiParent parent);
    
    public Rect toScreenRect(GuiControl control, Rect rect);
    
    public Rect toLayerRect(GuiControl control, Rect rect);
    
}
