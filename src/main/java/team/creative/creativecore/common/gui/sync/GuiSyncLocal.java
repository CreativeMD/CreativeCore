package team.creative.creativecore.common.gui.sync;

import java.util.function.Consumer;

import net.minecraft.nbt.Tag;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.packet.ControlSyncPacket;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder.GuiSyncHolderLayer;

public class GuiSyncLocal<T extends Tag> extends GuiSyncControl<GuiLayer, T> {
    
    private final Consumer<T> consumer;
    
    GuiSyncLocal(GuiSyncHolder holder, String name, Consumer<T> consumer) {
        super(holder, name);
        this.consumer = consumer;
    }
    
    @Override
    public void receive(GuiLayer layer, T tag) {
        this.consumer.accept(tag);
    }
    
    public void send(T tag) {
        GuiControl control = ((GuiSyncHolderLayer) holder).parent;
        control.getIntegratedParent().send(new ControlSyncPacket(control, this, tag));
    }
    
}
