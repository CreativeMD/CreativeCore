package team.creative.creativecore.common.network;

import java.util.HashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import team.creative.creativecore.CreativeCore;

public class CreativeNetwork {
    public final ResourceLocation CHANNEL;
    private final HashMap<Class<? extends CreativePacket>, CreativeNetworkPacket> packetTypes = new HashMap<>();
    private final Logger logger;
    
    private final String modid;
    private int id = 0;
    
    public CreativeNetwork(int version, Logger logger, ResourceLocation location) {
        this.logger = logger;
        this.CHANNEL = location;
        this.modid = location.getNamespace();
        this.logger.debug("Created network " + location + "");
    }
    
    public <T extends CreativePacket> void registerType(Class<T> classType, Supplier<T> supplier) {
        CreativeNetworkPacket<T> handler = new CreativeNetworkPacket<>(new ResourceLocation(modid, "" + id), classType, supplier, true);
        
        PayloadTypeRegistry.playC2S().register(handler.sid, StreamCodec.ofMember((x, y) -> handler.write(x, y, PacketFlow.CLIENTBOUND), x -> {
            T packet = handler.read(x, PacketFlow.CLIENTBOUND);
            packet.setType(handler.sid);
            return packet;
        }));
        PayloadTypeRegistry.playS2C().register(handler.sid, StreamCodec.ofMember((x, y) -> handler.write(x, y, PacketFlow.SERVERBOUND), x -> {
            T packet = handler.read(x, PacketFlow.SERVERBOUND);
            packet.setType(handler.sid);
            return packet;
        }));
        
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            CreativeNetworkClient.registerClientType(handler);
        
        ServerPlayNetworking.registerGlobalReceiver(handler.sid, (payload, context) -> {
            try {
                context.player().getServer().execute(() -> payload.executeServer(context.player()));
            } catch (Exception e) {
                CreativeCore.LOGGER.error("Failed to handle packet " + handler.sid.id(), e);
            }
        });
        
        packetTypes.put(classType, handler);
        id++;
    }
    
    public CreativeNetworkPacket getPacketType(Class<? extends CreativePacket> clazz) {
        return packetTypes.get(clazz);
    }
    
    protected <T extends CreativePacket> T prepare(T packet, PacketFlow flow) {
        packet.setType(packetTypes.get(packet.getClass()).sid);
        return packet;
    }
    
    public void sendToServer(CreativePacket message) {
        ClientPlayNetworking.send(prepare(message, PacketFlow.CLIENTBOUND));
    }
    
    public void sendToClient(CreativePacket message, ServerPlayer player) {
        ServerPlayNetworking.send(player, prepare(message, PacketFlow.SERVERBOUND));
    }
    
    public void sendToClientAll(MinecraftServer server, CreativePacket message) {
        prepare(message, PacketFlow.SERVERBOUND);
        for (ServerPlayer player : PlayerLookup.all(server))
            ServerPlayNetworking.send(player, message);
    }
}
