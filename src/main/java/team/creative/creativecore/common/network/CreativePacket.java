package team.creative.creativecore.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class CreativePacket implements CustomPacketPayload {
    
    private Type<CreativePacket> type;
    
    public CreativePacket() {}
    
    public void execute(Player player) {
        if (player.level().isClientSide)
            executeClient(player);
        else
            executeServer((ServerPlayer) player);
    }
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return type;
    }
    
    void setType(CustomPacketPayload.Type<CreativePacket> type) {
        this.type = type;
    }
    
    public abstract void executeClient(Player player);
    
    public abstract void executeServer(ServerPlayer player);
    
    public void requiresClient(Player player) {
        if (!player.level().isClientSide)
            throw new InvalidSideException(player);
    }
    
    public void requiresServer(Player player) {
        if (player.level().isClientSide)
            throw new InvalidSideException(player);
    }
    
}
