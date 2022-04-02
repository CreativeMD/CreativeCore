package team.creative.creativecore.common.gui.handler;

import net.minecraft.nbt.CompoundTag;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

@FunctionalInterface
public abstract interface GuiLayerHandler {
    
    public static final NamedHandlerRegistry<GuiLayerHandler> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static GuiLayer create(IGuiIntegratedParent parent, String id, CompoundTag nbt) {
        GuiLayerHandler handler = REGISTRY.get(id);
        if (handler != null)
            return handler.create(parent, nbt);
        return null;
    }
    
    public abstract GuiLayer create(IGuiIntegratedParent parent, CompoundTag nbt);
    
}
