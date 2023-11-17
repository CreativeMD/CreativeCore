package team.creative.creativecore.common.gui.packet;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.sync.GuiSync;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;

public class SyncPacket extends LayerPacket {
    
    public String path;
    public Tag tag;
    
    public SyncPacket(GuiSync sync, Tag tag) {
        this.path = sync.syncPath();
        this.tag = tag;
    }
    
    public SyncPacket() {}
    
    @Override
    public void execute(Player player, IGuiIntegratedParent container) {
        try {
            GuiSync sync = GuiSyncHolder.followPath(path, container);
            if (sync == null)
                throw new RuntimeException("Could not find sync " + path);
            sync.receive(container, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
