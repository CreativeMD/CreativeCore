package team.creative.creativecore.common.gui.packet;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.sync.GuiSync;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;

public class ControlSyncPacket extends LayerPacket {
    
    public String path;
    public String control;
    public Tag tag;
    
    public ControlSyncPacket(GuiControl control, GuiSync sync, Tag tag) {
        this.path = sync.syncPath();
        this.control = control.getNestedName();
        this.tag = tag;
    }
    
    public ControlSyncPacket() {}
    
    @Override
    public void execute(Player player, IGuiIntegratedParent container) {
        GuiSync sync = GuiSyncHolder.followPath(path, container);
        GuiControl syncControl = container.get(control);
        if (syncControl == null)
            throw new RuntimeException("Could not find control " + path);
        sync.receive(syncControl, tag);
    }
    
}
