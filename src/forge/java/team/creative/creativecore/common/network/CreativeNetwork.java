package team.creative.creativecore.common.network;

import java.util.HashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.Channel.VersionTest.Status;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import team.creative.creativecore.common.level.ISubLevel;

public class CreativeNetwork {

    @OnlyIn(value = Dist.CLIENT)
    private static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    private final HashMap<Class<? extends CreativePacket>, CreativeNetworkPacket> packetTypes = new HashMap<>();
    private final Logger logger;

    private int id = 0;

    public final SimpleChannel instance;

    public CreativeNetwork(int version, Logger logger, ResourceLocation location) {
        this.logger = logger;
        this.instance = ChannelBuilder.named(location).networkProtocolVersion(version).acceptedVersions((x, y) -> x == Status.PRESENT && y == version).simpleChannel();
        this.logger.debug("Created network " + location + "");
    }

    public <T extends CreativePacket> void registerType(Class<T> classType, Supplier<T> supplier) {
        CreativeNetworkPacket<T> handler = new CreativeNetworkPacket<>(classType, supplier);
        this.instance.messageBuilder(classType, id, NetworkDirection.PLAY_TO_CLIENT).encoder((message, buffer) -> handler.write(message, buffer)).decoder(buffer -> handler.read(
            buffer)).consumerMainThread((message, ctx) -> {
                try {
                    message.execute(ctx.getSender() == null ? getClientPlayer() : ctx.getSender());
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw e;
                }
            }).add();
        packetTypes.put(classType, handler);
        id++;
    }
    
    public CreativeNetworkPacket getPacketType(Class<? extends CreativePacket> clazz) {
        return packetTypes.get(clazz);
    }

    public void sendToServer(CreativePacket message) {
        this.instance.send(message, PacketDistributor.SERVER.noArg());
    }

    public void sendToClient(CreativePacket message, ServerPlayer player) {
        this.instance.send(message, PacketDistributor.PLAYER.with(player));
    }

    public void sendToClient(CreativePacket message, Level level, BlockPos pos) {
        if (level instanceof ISubLevel)
            sendToClientTracking(message, ((ISubLevel) level).getHolder());
        else

            sendToClient(message, level.getChunkAt(pos));
    }

    public void sendToClient(CreativePacket message, LevelChunk chunk) {
        this.instance.send(message, PacketDistributor.TRACKING_CHUNK.with(chunk));
    }

    public void sendToClientTracking(CreativePacket message, Entity entity) {
        if (entity.level() instanceof ISubLevel sub)
            sendToClientTracking(message, sub.getHolder());
        else
            this.instance.send(message, PacketDistributor.TRACKING_ENTITY.with(entity));
    }

    public void sendToClientTrackingAndSelf(CreativePacket message, Entity entity) {
        if (entity.level() instanceof ISubLevel sub)
            sendToClientTrackingAndSelf(message, sub.getHolder());
        else
            this.instance.send(message, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }

    public void sendToClientAll(MinecraftServer server, CreativePacket message) {
        this.instance.send(message, PacketDistributor.ALL.noArg());
    }
}
