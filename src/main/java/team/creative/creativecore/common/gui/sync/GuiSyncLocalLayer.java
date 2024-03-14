package team.creative.creativecore.common.gui.sync;

import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.packet.ControlSyncPacket;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder.GuiSyncHolderLayer;

public class GuiSyncLocalLayer<T extends GuiLayer> extends GuiSyncControl<GuiLayer, CompoundTag> {
    
    private final Function<CompoundTag, T> creator;
    
    GuiSyncLocalLayer(GuiSyncHolder holder, String name, Function<CompoundTag, T> creator) {
        super(holder, name);
        this.creator = creator;
    }
    
    @Override
    public void receive(GuiLayer layer, CompoundTag tag) {
        layer.getIntegratedParent().openLayer(creator.apply(tag));
    }
    
    public T open(CompoundTag tag) {
        T layer = creator.apply(tag);
        GuiControl control = ((GuiSyncHolderLayer) holder).parent;
        IGuiIntegratedParent parent = control.getIntegratedParent();
        parent.openLayer(layer);
        parent.send(new ControlSyncPacket(control, this, tag));
        return layer;
    }
    
}
