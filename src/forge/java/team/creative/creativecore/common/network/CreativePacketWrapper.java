package team.creative.creativecore.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CreativePacketWrapper<T extends CreativePacket>(CreativeNetworkPacket<T> packetType, T packet) implements CustomPacketPayload {
    
    @Override
    public void write(FriendlyByteBuf buffer) {
        packetType.write(packet, buffer);
    }
    
    @Override
    public ResourceLocation id() {
        return packetType.id;
    }
    
}
