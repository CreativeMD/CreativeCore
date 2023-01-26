package team.creative.creativecore.common.gui.sync;

import java.util.function.BiConsumer;

import net.minecraft.nbt.Tag;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.packet.ControlSyncPacket;

public class GuiSyncGlobal<C extends GuiControl, T extends Tag> extends GuiSyncControl<C, T> {
    
    private final BiConsumer<C, T> consumer;
    
    GuiSyncGlobal(GuiSyncHolder holder, String name, BiConsumer<C, T> consumer) {
        super(holder, name);
        this.consumer = consumer;
    }
    
    @Override
    public void receive(C control, T tag) {
        this.consumer.accept(control, tag);
    }
    
    public void send(C control, T tag) {
        control.getIntegratedParent().send(new ControlSyncPacket(control, this, tag));
    }
    
    public void sendAndExecute(C control, T tag) {
        send(control, tag);
        receive(control, tag);
    }
    
}
