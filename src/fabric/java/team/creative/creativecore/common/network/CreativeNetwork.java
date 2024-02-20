package team.creative.creativecore.common.network;

import java.util.HashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

public class CreativeNetwork {
    public final ResourceLocation CHANNEL;
    private final HashMap<Class<? extends CreativePacket>, CreativeNetworkPacket> packetTypes = new HashMap<>();
    private final HashMap<Class<? extends CreativePacket>, ResourceLocation> packetTypeChannels = new HashMap<>();
    private final Logger logger;
    
    private final String modid;
    private int id = 0;
    
    public CreativeNetwork(int version, Logger logger, ResourceLocation location) {
        this.logger = logger;
        this.CHANNEL = location;
        this.modid = location.getNamespace();
        this.logger.debug("Created network " + location + "");
    }
    
    @Environment(EnvType.CLIENT)
    private static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
    
    private static class ClientHandler extends ServerHandler {
        @Environment(EnvType.CLIENT)
        @Override
        public <T extends CreativePacket> void register(ResourceLocation CURR_CHANNEL, CreativeNetworkPacket<T> packet_handler) {
            super.register(CURR_CHANNEL, packet_handler);
            ClientPlayNetworking.registerGlobalReceiver(CURR_CHANNEL, new Handler<T>(packet_handler));
        }
        
        @Environment(EnvType.CLIENT)
        private static class Handler<T extends CreativePacket> implements ClientPlayNetworking.PlayChannelHandler {
            private final CreativeNetworkPacket<T> packet_handler;
            
            Handler(CreativeNetworkPacket<T> packet) {
                packet_handler = packet;
            }
            
            @Override
            public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
                var message = packet_handler.read(buf);
                client.execute(() -> {
                    try {
                        message.execute(client.player);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
    
    private static class ServerHandler {
        public static ServerHandler INSTANCE = new ClientHandler();
        
        public <T extends CreativePacket> void register(ResourceLocation CURR_CHANNEL, CreativeNetworkPacket<T> packet_handler) {
            ServerPlayNetworking.registerGlobalReceiver(CURR_CHANNEL,
                (MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
                    var message = packet_handler.read(buf);
                    server.execute(() -> {
                        message.execute(handler.getPlayer());
                    });
                });
        }
    }
    
    public <T extends CreativePacket> void registerType(Class<T> classType, Supplier<T> supplier) {
        int CURR_ID = id++;
        ResourceLocation CURR_CHANNEL = new ResourceLocation(CHANNEL.getNamespace(), CHANNEL.getPath() + CURR_ID);
        CreativeNetworkPacket<T> packet_handler = new CreativeNetworkPacket<>(new ResourceLocation(modid, "" + id), classType, supplier);
        ServerHandler.INSTANCE.register(CURR_CHANNEL, packet_handler);
        packetTypes.put(classType, packet_handler);
        packetTypeChannels.put(classType, CURR_CHANNEL);
    }
    
    public CreativeNetworkPacket getPacketType(Class<? extends CreativePacket> clazz) {
        return packetTypes.get(clazz);
    }
    
    public void sendToServer(CreativePacket message) {
        var BUFF = new FriendlyByteBuf(Unpooled.buffer());
        packetTypes.get(message.getClass()).write(message, BUFF);
        ClientPlayNetworking.send(packetTypeChannels.get(message.getClass()), BUFF);
    }
    
    public void sendToClient(CreativePacket message, ServerPlayer player) {
        var BUFF = new FriendlyByteBuf(Unpooled.buffer());
        packetTypes.get(message.getClass()).write(message, BUFF);
        ServerPlayNetworking.send(player, packetTypeChannels.get(message.getClass()), BUFF);
    }
    
    public void sendToClientAll(MinecraftServer server, CreativePacket message) {
        var BUFF = new FriendlyByteBuf(Unpooled.buffer());
        packetTypes.get(message.getClass()).write(message, BUFF);
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, packetTypeChannels.get(message.getClass()), BUFF);
        }
    }
}
