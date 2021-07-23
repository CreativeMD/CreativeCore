package team.creative.creativecore.common.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
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
    
    public <T extends CreativePacket> void registerType(Class<T> classType) {
        CreativeBufferHandler<T> handler = new CreativeBufferHandler<>(classType);
        this.instance.registerMessage(id, classType, (message, buffer) -> {
            handler.write(message, buffer);
        }, (buffer) -> {
            return handler.read(buffer);
        }, (message, ctx) -> {
            ctx.get().enqueueWork(() -> message.execute(ctx.get().getSender() == null ? getClientPlayer() : ctx.get().getSender()));
            ctx.get().setPacketHandled(true);
        });
        id++;
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
    
    public static class CreativeBufferHandler<T extends CreativePacket> {
        
        public final Class<T> classType;
        public List<CreativeFieldParserEntry> parsers = new ArrayList<>();
        
        public CreativeBufferHandler(Class<T> classType) {
            this.classType = classType;
            
            for (Field field : this.classType.getFields()) {
                
                if (Modifier.isTransient(field.getModifiers()) && field.isAnnotationPresent(OnlyIn.class))
                    continue;
                
                CreativeFieldParserEntry parser = CreativeFieldParserEntry.getParser(field);
                if (parser != null)
                    parsers.add(parser);
                else
                    throw new RuntimeException("Could not find parser for " + classType.getName() + "." + field.getName() + "! type: " + field.getType().getName());
            }
        }
        
        public void write(T packet, FriendlyByteBuf buffer) {
            for (CreativeFieldParserEntry parser : parsers)
                parser.write(packet, buffer);
        }
        
        public T read(FriendlyByteBuf buffer) {
            try {
                Constructor<T> constructor = classType.getConstructor();
                T message = constructor.newInstance();
                
                for (CreativeFieldParserEntry parser : parsers)
                    parser.read(message, buffer);
                
                return message;
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
