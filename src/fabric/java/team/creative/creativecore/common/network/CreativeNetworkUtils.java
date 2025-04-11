package team.creative.creativecore.common.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;

public class CreativeNetworkUtils {
    
    public static <T extends PacketListener> List<Packet<? super T>> flatten(Iterable<Packet<? super T>> packets) {
        final List<Packet<? super T>> result = new ArrayList<>();
        packets.forEach(packet -> {
            if (packet instanceof BundlePacket<? super T> innerBundle)
                result.addAll(flatten(innerBundle.subPackets()));
            else
                result.add(packet);
        });
        return result;
    }
}
