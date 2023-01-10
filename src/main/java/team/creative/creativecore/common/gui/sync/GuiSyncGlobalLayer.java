package team.creative.creativecore.common.gui.sync;

import java.util.function.BiFunction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.packet.ControlSyncPacket;

public class GuiSyncGlobalLayer<C extends GuiControl, T extends GuiLayer> extends GuiSync<C, CompoundTag> {
    
    private final BiFunction<C, CompoundTag, T> creator;
    
    GuiSyncGlobalLayer(GuiSyncHolder holder, String name, BiFunction<C, CompoundTag, T> creator) {
        super(holder, name);
        this.creator = creator;
    }
    
    @Override
    public void receive(C control, CompoundTag tag) {
        control.getIntegratedParent().openLayer(creator.apply(control, tag));
    }
    
    public T open(C control, CompoundTag tag) {
        T layer = creator.apply(control, tag);
        layer.getIntegratedParent().openLayer(layer);
        if (control.isClient())
            CreativeCore.NETWORK.sendToServer(new ControlSyncPacket(control, this, tag));
        else
            CreativeCore.NETWORK.sendToClient(new ControlSyncPacket(control, this, tag), (ServerPlayer) control.getPlayer());
        return layer;
    }
    
}
