package team.creative.creativecore.common.gui.sync;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.network.CreativePacket;

public abstract class LayerPacket extends CreativePacket {
    
    public LayerPacket() {
        
    }
    
    @Override
    public void executeClient(Player player) {
        if (player != null && player.containerMenu instanceof IGuiIntegratedParent)
            execute(player, (IGuiIntegratedParent) player.containerMenu);
        else if (Minecraft.getInstance().screen instanceof IGuiIntegratedParent)
            execute(player, (IGuiIntegratedParent) Minecraft.getInstance().screen);
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        if (player.containerMenu instanceof IGuiIntegratedParent)
            execute(player, (IGuiIntegratedParent) player.containerMenu);
    }
    
    public abstract void execute(Player player, IGuiIntegratedParent container);
    
}
