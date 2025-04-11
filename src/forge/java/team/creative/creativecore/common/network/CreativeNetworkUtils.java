package team.creative.creativecore.common.network;

import java.util.List;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.neoforged.neoforge.network.bundle.BundlePacketUtils;

public class CreativeNetworkUtils {
    
    public static <T extends PacketListener> List<Packet<? super T>> flatten(Iterable<Packet<? super T>> packets) {
        return BundlePacketUtils.flatten(packets);
    }
}
