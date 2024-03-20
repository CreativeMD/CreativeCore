package team.creative.creativecore.common.gui.sync;

import java.util.function.BiConsumer;

import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.packet.ControlSyncPacket;

public class GuiSyncGlobal<C extends GuiControl, T extends Tag> extends GuiSync<C, T> {
    
    private final BiConsumer<C, T> consumer;
    
    GuiSyncGlobal(GuiSyncHolder holder, String name, BiConsumer<C, T> consumer) {
        super(holder, name);
        this.consumer = consumer;
    }
    
    @Override
    public void receive(C control, T tag) {
        this.consumer.accept(control, tag);
    }
    
    public void send(GuiControl control, T tag) {
        if (control.isClient())
            CreativeCore.NETWORK.sendToServer(new ControlSyncPacket(control, this, tag));
        else
            CreativeCore.NETWORK.sendToClient(new ControlSyncPacket(control, this, tag), (ServerPlayer) control.getPlayer());
    }
    
}
