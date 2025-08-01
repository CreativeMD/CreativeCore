package team.creative.creativecore.common.gui.sync;

import org.apache.commons.lang3.function.TriFunction;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.packet.SyncPacket;

public class GuiSyncGlobalLayer<T extends GuiLayer> extends GuiSync<CompoundTag> {
    
    private final TriFunction<Boolean, HolderLookup.Provider, CompoundTag, T> creator;
    
    GuiSyncGlobalLayer(GuiSyncHolder holder, String name, TriFunction<Boolean, HolderLookup.Provider, CompoundTag, T> creator) {
        super(holder, name);
        this.creator = creator;
    }
    
    @Override
    public void receive(IGuiIntegratedParent parent, CompoundTag tag) {
        parent.openLayer(creator.apply(parent.isClient(), parent.provider(), tag));
    }
    
    public T open(IGuiIntegratedParent parent, CompoundTag tag) {
        T layer = creator.apply(parent.isClient(), parent.provider(), tag);
        parent.openLayer(layer);
        parent.send(new SyncPacket(this, tag));
        return layer;
    }
    
}
