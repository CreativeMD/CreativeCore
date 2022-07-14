package team.creative.creativecore.common.gui.creator;

import net.minecraft.nbt.CompoundTag;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

@FunctionalInterface
public abstract interface GuiLayerCreator {
    
    public static final NamedHandlerRegistry<GuiLayerCreator> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static GuiLayer create(IGuiIntegratedParent parent, String id, CompoundTag nbt) {
        GuiLayerCreator handler = REGISTRY.get(id);
        if (handler != null)
            return handler.create(parent, nbt);
        return null;
    }
    
    public abstract GuiLayer create(IGuiIntegratedParent parent, CompoundTag nbt);
    
}
