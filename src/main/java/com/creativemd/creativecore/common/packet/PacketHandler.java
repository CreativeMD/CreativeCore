package com.creativemd.creativecore.common.packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.packet.CreativeMessageHandler.MessageType;
import com.google.common.base.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
	
	public static void sendPacketToTrackingPlayers(CreativeCorePacket packet, EntityPlayerMP player) {
		Set<? extends EntityPlayer> players = ((EntityPlayerMP) player).getServerWorld().getEntityTracker().getTrackingPlayers(player);
		for (Iterator iterator = players.iterator(); iterator.hasNext();) {
			EntityPlayer entityPlayer = (EntityPlayer) iterator.next();
			sendPacketToPlayer(packet, (EntityPlayerMP) entityPlayer);
		}
	}
	
}
