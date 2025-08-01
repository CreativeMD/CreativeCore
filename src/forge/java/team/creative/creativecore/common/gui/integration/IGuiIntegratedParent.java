package team.creative.creativecore.common.gui.integration;

import java.util.List;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.creativecore.common.util.math.geo.Rect;

public interface IGuiIntegratedParent extends IGuiParent {
    
    public GuiLayer EMPTY_CLIENT = new GuiLayer(true, "empty") {
        
        @Override
        public void create() {}
        
    };
    
    public GuiLayer EMPTY_SERVER = new GuiLayer(false, "empty") {
        
        @Override
        public void create() {}
        
    };
    
    public HolderLookup.Provider provider();
    
    public List<GuiLayer> getLayers();
    
    public GuiLayer getTopLayer();
    
    public default boolean isOpen(Class<? extends GuiLayer> clazz) {
        for (GuiLayer layer : getLayers())
            if (clazz.isInstance(layer))
                return true;
        return false;
    }
    
    @Override
    public default void raiseEvent(GuiEvent event) {}
    
    @Override
    public default void reflow() {}
    
    public void openLayer(GuiLayer layer);
    
    public void closeLayer(int layer);
    
    @Override
    public default boolean isParent(IGuiParent parent) {
        return parent == this;
    }
    
    @Override
    public default boolean hasGui() {
        return true;
    }
    
    public default GuiControl get(String control) {
        for (GuiLayer layer : getLayers())
            if (control.startsWith(layer.getNestedName()))
                if (control.equals(layer.getNestedName()))
                    return layer;
                else
                    return layer.get(control.substring(layer.getNestedName().length() + 1));
        return null;
    }
    
    @Override
    public default Rect toLayerRect(GuiControl control, Rect rect) {
        return rect;
    }
    
    @Override
    public default IGuiIntegratedParent getIntegratedParent() {
        return this;
    }
    
    public void send(CreativePacket message);
    
}
