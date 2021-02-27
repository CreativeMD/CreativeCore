package team.creative.creativecore.common.gui.handler;

import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;

public abstract class GuiLayerHandler {
    
    private static HashMap<String, GuiLayerHandler> handlers = new HashMap<>();
    
    public static void registerGuiLayerHandler(String id, GuiLayerHandler handler) {
        handlers.put(id, handler);
    }
    
    public static GuiLayer create(IGuiIntegratedParent parent, String id, CompoundNBT nbt) {
        GuiLayerHandler handler = handlers.get(id);
        if (handler != null)
            return handler.create(parent, nbt);
        return null;
    }
    
    public abstract GuiLayer create(IGuiIntegratedParent parent, CompoundNBT nbt);
    
}
