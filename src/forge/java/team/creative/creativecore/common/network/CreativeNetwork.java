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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.level.ISubLevel;

public class CreativeNetwork {
    
    @OnlyIn(Dist.CLIENT)
    private static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
    
    private HashMap<Class<? extends CreativePacket>, CreativeNetworkPacket> packetTypes = new HashMap<>();
    
    private final Logger logger;
    private final String modid;
    private String version;
    
    private IPayloadRegistrar registrar;
    
    private int id = 0;
    
    public CreativeNetwork(int version, Logger logger, ResourceLocation location) {
        this.logger = logger;
        this.version = "" + version;
        this.modid = location.getNamespace();
        this.logger.debug("Created network " + location + "");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::register);
    }
    
    public void register(final RegisterPayloadHandlerEvent event) {
        registrar = event.registrar(modid).versioned(version);
        for (CreativeNetworkPacket packet : packetTypes.values())
            registerType(packet);
    }
    
    private <T extends CreativePacket> void registerType(CreativeNetworkPacket handler) {
        registrar.play(handler.id, buffer -> new CreativePacketWrapper<>(handler, handler.read(buffer)), (message, ctx) -> {
            try {
                ctx.workHandler().execute(() -> message.packet().execute(ctx.player().isPresent() ? ctx.player().get() : getClientPlayer()));
            } catch (Throwable e) {
                CreativeCore.LOGGER.error(e);
                throw e;
            }
        });
    }
    
    public <T extends CreativePacket> void registerType(Class<T> classType, Supplier<T> supplier) {
        CreativeNetworkPacket handler = new CreativeNetworkPacket(new ResourceLocation(modid, "" + id), classType, supplier);
        packetTypes.put(classType, handler);
        if (registrar != null)
            registerType(handler);
        id++;
    }
    
    protected <T extends CreativePacket> CreativePacketWrapper<T> wrap(T packet) {
        return new CreativePacketWrapper<>(packetTypes.get(packet.getClass()), packet);
    }
    
    public void sendToServer(CreativePacket message) {
        PacketDistributor.SERVER.noArg().send(wrap(message));
    }
    
    public void sendToClient(CreativePacket message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(wrap(message));
    }
    
    public void sendToClient(CreativePacket message, Level level, BlockPos pos) {
        if (level instanceof ISubLevel)
            sendToClientTracking(message, ((ISubLevel) level).getHolder());
        else
            
            sendToClient(message, level.getChunkAt(pos));
    }
    
    public void sendToClient(CreativePacket message, LevelChunk chunk) {
        PacketDistributor.TRACKING_CHUNK.with(chunk).send(wrap(message));
    }
    
    public void sendToClientTracking(CreativePacket message, Entity entity) {
        if (entity.level() instanceof ISubLevel sub)
            sendToClientTracking(message, sub.getHolder());
        else
            PacketDistributor.TRACKING_ENTITY.with(entity).send(wrap(message));
    }
    
    public void sendToClientTrackingAndSelf(CreativePacket message, Entity entity) {
        if (entity.level() instanceof ISubLevel sub)
            sendToClientTrackingAndSelf(message, sub.getHolder());
        else
            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity).send(wrap(message));
    }
    
    public void sendToClientAll(MinecraftServer server, CreativePacket message) {
        PacketDistributor.ALL.noArg().send(wrap(message));
    }
}
