package team.creative.creativecore.common.network;

import java.util.HashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.level.ISubLevel;

public class CreativeNetwork {
    
    private final HashMap<Class<? extends CreativePacket>, CreativeNetworkPacket> packetTypes = new HashMap<>();
    
    private final Logger logger;
    private final String modid;
    private String version;
    
    private PayloadRegistrar registrar;
    
    private int id = 0;
    
    public CreativeNetwork(int version, Logger logger, ResourceLocation location) {
        this.logger = logger;
        this.version = "" + version;
        this.modid = location.getNamespace();
        this.logger.debug("Created network " + location + "");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::register);
    }
    
    public void register(final RegisterPayloadHandlersEvent event) {
        registrar = event.registrar(modid).versioned(version).optional();
        for (CreativeNetworkPacket packet : packetTypes.values())
            registerType(packet);
    }
    
    private <T extends CreativePacket> void registerType(CreativeNetworkPacket<CreativePacket> handler) {
        IPayloadHandler<CreativePacket> executor = (packet, ctx) -> {
            try {
                ctx.enqueueWork(() -> packet.execute(ctx.player()));
            } catch (Throwable e) {
                CreativeCore.LOGGER.error("Executing a packet ran into an exception", e);
                throw e;
            }
        };
        registrar.playToServer(handler.cid, StreamCodec.ofMember((x, y) -> handler.write(x, y, PacketFlow.CLIENTBOUND), x -> handler.read(x, PacketFlow.CLIENTBOUND)), executor);
        registrar.playToClient(handler.sid, StreamCodec.ofMember((x, y) -> handler.write(x, y, PacketFlow.SERVERBOUND), x -> handler.read(x, PacketFlow.SERVERBOUND)), executor);
    }
    
    public <T extends CreativePacket> void registerType(Class<T> classType, Supplier<T> supplier) {
        CreativeNetworkPacket handler = new CreativeNetworkPacket(new ResourceLocation(modid, "" + id), classType, supplier, false);
        packetTypes.put(classType, handler);
        if (registrar != null)
            registerType(handler);
        id++;
    }
    
    protected <T extends CreativePacket> T prepare(T packet, PacketFlow flow) {
        packet.setType(flow.isClientbound() ? packetTypes.get(packet.getClass()).cid : packetTypes.get(packet.getClass()).sid);
        return packet;
    }
    
    public void sendToServer(CreativePacket message) {
        PacketDistributor.sendToServer(prepare(message, PacketFlow.CLIENTBOUND));
    }
    
    public void sendToClient(CreativePacket message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, prepare(message, PacketFlow.SERVERBOUND));
    }
    
    public void sendToClient(CreativePacket message, Level level, BlockPos pos) {
        if (level instanceof ISubLevel)
            sendToClientTracking(message, ((ISubLevel) level).getHolder());
        else
            sendToClient(message, level.getChunkAt(pos));
    }
    
    public void sendToClient(CreativePacket message, LevelChunk chunk) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) chunk.getLevel(), chunk.getPos(), prepare(message, PacketFlow.SERVERBOUND));
    }
    
    public void sendToClientTracking(CreativePacket message, Entity entity) {
        if (entity.level() instanceof ISubLevel sub)
            sendToClientTracking(message, sub.getHolder());
        else
            PacketDistributor.sendToPlayersTrackingEntity(entity, prepare(message, PacketFlow.SERVERBOUND));
    }
    
    public void sendToClientTrackingAndSelf(CreativePacket message, Entity entity) {
        if (entity.level() instanceof ISubLevel sub)
            sendToClientTrackingAndSelf(message, sub.getHolder());
        else
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, prepare(message, PacketFlow.SERVERBOUND));
    }
    
    public void sendToClientAll(MinecraftServer server, CreativePacket message) {
        PacketDistributor.sendToAllPlayers(prepare(message, PacketFlow.SERVERBOUND));
    }
}
