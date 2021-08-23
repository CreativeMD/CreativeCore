package team.creative.creativecore.common.network;

import java.util.HashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class CreativeNetwork {
    
    @OnlyIn(value = Dist.CLIENT)
    private static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
    
    private final HashMap<Class<? extends CreativePacket>, CreativeNetworkPacket> packetTypes = new HashMap<>();
    private final Logger logger;
    private final String version;
    
    private int id = 0;
    
    public final SimpleChannel instance;
    
    public CreativeNetwork(String version, Logger logger, ResourceLocation location) {
        this.version = version;
        this.logger = logger;
        this.instance = NetworkRegistry.newSimpleChannel(location, () -> this.version, x -> true/* this.version::equals*/, this.version::equals);
        this.logger.debug("Created network " + location + "");
    }
    
    public <T extends CreativePacket> void registerType(Class<T> classType, Supplier<T> supplier) {
        CreativeNetworkPacket<T> handler = new CreativeNetworkPacket<>(classType, supplier);
        this.instance.registerMessage(id, classType, (message, buffer) -> {
            handler.write(message, buffer);
        }, (buffer) -> {
            return handler.read(buffer);
        }, (message, ctx) -> {
            ctx.get().enqueueWork(() -> message.execute(ctx.get().getSender() == null ? getClientPlayer() : ctx.get().getSender()));
            ctx.get().setPacketHandled(true);
        });
        packetTypes.put(classType, handler);
        id++;
    }
    
    public CreativeNetworkPacket getPacketType(Class<? extends CreativePacket> clazz) {
        return packetTypes.get(clazz);
    }
    
    public void sendToServer(CreativePacket message) {
        this.instance.sendToServer(message);
    }
    
    public void sendToClient(CreativePacket message, ServerPlayer player) {
        this.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    public void sendToClient(CreativePacket message, LevelChunk chunk) {
        this.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    }
    
    public void sendToClientTracking(CreativePacket message, Entity entity) {
        this.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }
    
    public void sendToClientAll(CreativePacket message) {
        this.instance.send(PacketDistributor.ALL.noArg(), message);
    }
}
