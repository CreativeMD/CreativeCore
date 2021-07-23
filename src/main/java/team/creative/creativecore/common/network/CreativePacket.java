package team.creative.creativecore.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class CreativePacket {
    
    public CreativePacket() {
        
    }
    
    public void execute(Player player) {
        if (player.level.isClientSide)
            executeClient(player);
        else
            executeServer((ServerPlayer) player);
    }
    
    public abstract void executeClient(Player player);
    
    public abstract void executeServer(ServerPlayer player);
    
}
