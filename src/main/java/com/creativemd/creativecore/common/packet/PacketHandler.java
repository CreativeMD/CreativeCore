package com.creativemd.creativecore.common.packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.core.CreativeCoreDummy;
import com.google.common.base.Predicate;

import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketHandler {	
	public static void sendPacketToAllPlayers(CreativeCorePacket packet)
	{
		CreativeCore.network.sendToAll(new CreativeMessageHandler(packet));
	}
	
	public static void sendPacketToServer(CreativeCorePacket packet)
	{
		CreativeCore.network.sendToServer(new CreativeMessageHandler(packet));
	}
	
	public static void sendPacketsToAllPlayers(ArrayList<CreativeCorePacket> packets)
	{
		for (int i = 0; i < packets.size(); i++) {
			sendPacketToAllPlayers(packets.get(i));
		}
	}
	
	public static void sendPacketToNearPlayers(World world, CreativeCorePacket packet, int distance, BlockPos pos)
	{
		for (EntityPlayerMP entityplayermp : world.getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>(){
            public boolean apply(@Nullable EntityPlayerMP p_apply_1_)
                {
                    return p_apply_1_.getDistanceSq(pos) < Math.pow(distance, 2);
                }
            }))
            {
            	sendPacketToPlayer(packet, entityplayermp);
            }
	}
	
	public static void sendPacketToPlayer(CreativeCorePacket packet, EntityPlayerMP player)
	{
		CreativeCore.network.sendTo(new CreativeMessageHandler(packet), player);
	}

	public static void sendPacketToTrackingPlayers(CreativeCorePacket packet, EntityPlayerMP player)
	{
		Set<? extends EntityPlayer> players = ((EntityPlayerMP) player).getServerWorld().getEntityTracker().getTrackingPlayers(player);
		for (Iterator iterator = players.iterator(); iterator.hasNext();) {
			EntityPlayer entityPlayer = (EntityPlayer) iterator.next();
			sendPacketToPlayer(packet, (EntityPlayerMP) entityPlayer);
		}
	}
	
}
