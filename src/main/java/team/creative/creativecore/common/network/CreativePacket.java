package team.creative.creativecore.common.network;

import net.minecraft.entity.player.PlayerEntity;

public abstract class CreativePacket {
    
    public CreativePacket() {
        
    }
    
    public void execute(PlayerEntity player) {
        if (player.level.isClientSide)
            executeClient(player);
        else
            executeServer(player);
    }
    
    public abstract void executeClient(PlayerEntity player);
    
    public abstract void executeServer(PlayerEntity player);
    
}
