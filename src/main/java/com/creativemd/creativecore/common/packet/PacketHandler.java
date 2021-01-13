package com.creativemd.creativecore.common.packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.packet.CreativeMessageHandler.MessageType;
import com.creativemd.creativecore.common.world.CreativeWorld;
import com.creativemd.creativecore.common.world.IOrientatedWorld;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketHandler {
    
    private static ArrayList<CreativeSplittedMessageHandler> queuedMessages = new ArrayList<>();
    
    public static void addQueueMessage(CreativeSplittedMessageHandler message) {
        queuedMessages.add(message);
    }
    
    private static void sendQueuedMessage(EntityPlayer player) {
        for (int i = 0; i < queuedMessages.size(); i++) {
            sendMessage(queuedMessages.get(i).type, player, queuedMessages.get(i));
        }
        queuedMessages.clear();
    }
    
    public static void sendMessage(MessageType type, EntityPlayer player, IMessage message) {
        switch (type) {
        case ToAllPlayer:
            CreativeCore.network.sendToAll(message);
            break;
        case ToPlayer:
            CreativeCore.network.sendTo(message, (EntityPlayerMP) player);
            break;
        case ToServer:
            CreativeCore.network.sendToServer(message);
            break;
        }
    }
    
    public static void sendPacketToAllPlayers(CreativeCorePacket packet) {
        CreativeCore.network.sendToAll(new CreativeMessageHandler(packet, MessageType.ToAllPlayer, null));
        sendQueuedMessage(null);
    }
    
    public static void sendPacketToServer(CreativeCorePacket packet) {
        CreativeCore.network.sendToServer(new CreativeMessageHandler(packet, MessageType.ToServer, null));
        sendQueuedMessage(null);
    }
    
    public static void sendPacketsToAllPlayers(List<CreativeCorePacket> packets) {
        for (int i = 0; i < packets.size(); i++) {
            sendPacketToAllPlayers(packets.get(i));
        }
    }
    
    public static void sendPacketToNearPlayers(World world, CreativeCorePacket packet, int distance, BlockPos pos) {
        for (EntityPlayerMP entityplayermp : world.getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>() {
            @Override
            public boolean apply(@Nullable EntityPlayerMP p_apply_1_) {
                return p_apply_1_.getDistanceSq(pos) < Math.pow(distance, 2);
            }
        })) {
            sendPacketToPlayer(packet, entityplayermp);
        }
    }
    
    public static void sendPacketToPlayer(CreativeCorePacket packet, EntityPlayerMP player) {
        CreativeCore.network.sendTo(new CreativeMessageHandler(packet, MessageType.ToPlayer, player), player);
        sendQueuedMessage(player);
    }
    
    public static void sendPacketToPlayers(CreativeCorePacket packet, Iterable<? extends EntityPlayer> players) {
        for (EntityPlayer player : players) {
            sendPacketToPlayer(packet, (EntityPlayerMP) player);
        }
    }
    
    public static void sendPacketToPlayers(CreativeCorePacket packet, Iterable<? extends EntityPlayer> players, @Nullable Predicate<EntityPlayer> predicate) {
        for (EntityPlayer player : players)
            if (predicate == null || predicate.apply(player))
                sendPacketToPlayer(packet, (EntityPlayerMP) player);
    }
    
    private static CreativeWorld getParentSubWorld(IOrientatedWorld world) {
        if (world.getParent() instanceof IOrientatedWorld)
            return getParentSubWorld((IOrientatedWorld) world.getParent());
        return (CreativeWorld) world;
    }
    
    public static void sendPacketToTrackingPlayers(CreativeCorePacket packet, World world, BlockPos pos, @Nullable Predicate<EntityPlayer> predicate) {
        if (world instanceof IOrientatedWorld) {
            CreativeWorld subWorld = getParentSubWorld((IOrientatedWorld) world);
            sendPacketToTrackingPlayers(packet, subWorld.parent, (WorldServer) ((IOrientatedWorld) world).getRealWorld(), predicate);
        } else {
            try {
                sendPacketToPlayers(packet, ((WorldServer) world).getPlayerChunkMap().getEntry(pos.getX() >> 4, pos.getZ() >> 4).getWatchingPlayers(), predicate);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void sendPacketToTrackingPlayers(CreativeCorePacket packet, Entity entity, WorldServer world, @Nullable Predicate<EntityPlayer> predicate) {
        for (EntityPlayer player : world.getEntityTracker().getTrackingPlayers(entity))
            if (predicate == null || predicate.apply(player))
                sendPacketToPlayer(packet, (EntityPlayerMP) player);
    }
    
    public static void sendPacketToTrackingPlayers(CreativeCorePacket packet, EntityPlayerMP player) {
        Set<? extends EntityPlayer> players = player.getServerWorld().getEntityTracker().getTrackingPlayers(player);
        for (Iterator iterator = players.iterator(); iterator.hasNext();) {
            EntityPlayer entityPlayer = (EntityPlayer) iterator.next();
            sendPacketToPlayer(packet, (EntityPlayerMP) entityPlayer);
        }
    }
    
}
