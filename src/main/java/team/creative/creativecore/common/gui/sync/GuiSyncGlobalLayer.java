package team.creative.creativecore.common.gui.sync;

import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.packet.SyncPacket;

public class GuiSyncGlobalLayer<T extends GuiLayer> extends GuiSync<CompoundTag> {
    
    private final Function<CompoundTag, T> creator;
    
    GuiSyncGlobalLayer(GuiSyncHolder holder, String name, Function<CompoundTag, T> creator) {
        super(holder, name);
        this.creator = creator;
    }
    
    @Override
    public void receive(IGuiIntegratedParent parent, CompoundTag tag) {
        parent.openLayer(creator.apply(tag));
    }
    
    public T open(IGuiIntegratedParent parent, CompoundTag tag) {
        T layer = creator.apply(tag);
        parent.openLayer(layer);
        if (parent.isClient())
            CreativeCore.NETWORK.sendToServer(new SyncPacket(this, tag));
        else
            CreativeCore.NETWORK.sendToClient(new SyncPacket(this, tag), (ServerPlayer) parent.getPlayer());
        return layer;
    }
    
}
