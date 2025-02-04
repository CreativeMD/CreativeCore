package team.creative.creativecore.common.network;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.GameProtocols;
import net.neoforged.neoforge.network.bundle.BundlePacketUtils;

public class CreativeNetworkUtils {
    
    public static StreamCodec<ByteBuf, Packet<? extends PacketListener>> getPacketCodec(RegistryFriendlyByteBuf buffer, PacketFlow flow) {
        return (StreamCodec<ByteBuf, Packet<? extends PacketListener>>) (Object) (flow != PacketFlow.CLIENTBOUND ? GameProtocols.CLIENTBOUND_TEMPLATE : GameProtocols.SERVERBOUND_TEMPLATE)
                .bind(RegistryFriendlyByteBuf.decorator(buffer.registryAccess(), buffer.getConnectionType())).codec();
    }
    
    public static <T extends PacketListener> List<Packet<? super T>> flatten(Iterable<Packet<? super T>> packets) {
        return BundlePacketUtils.flatten(packets);
    }
}
