package team.creative.creativecore.common.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import team.creative.creativecore.CreativeCore;

@Environment(EnvType.CLIENT)
public class CreativeNetworkClient {
    
    public static <T extends CreativePacket> void registerClientType(CreativeNetworkPacket<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(handler.sid, (payload, context) -> {
            try {
                context.client().execute(() -> payload.execute(context.player()));
            } catch (Exception e) {
                CreativeCore.LOGGER.error("Failed to handle packet " + handler.sid.id(), e);
            }
        });
    }
    
}
