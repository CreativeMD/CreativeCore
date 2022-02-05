package team.creative.creativecore.common.gui.sync;

import net.minecraft.nbt.Tag;
import team.creative.creativecore.common.gui.GuiControl;

public abstract class GuiSync<C extends GuiControl, T extends Tag> {
    
    public final GuiSyncHolder holder;
    public final String name;
    
    GuiSync(GuiSyncHolder holder, String name) {
        this.holder = holder;
        this.name = name;
    }
    
    public abstract void receive(C control, T tag);
    
    public String syncPath() {
        return holder.path() + name;
    }
    
}
