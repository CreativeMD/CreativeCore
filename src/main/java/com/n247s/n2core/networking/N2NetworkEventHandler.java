package com.n247s.n2core.networking;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.Logger;

import com.n247s.n2core.N2Core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class N2NetworkEventHandler
{
	private static final Logger log = N2Core.logger;
	private static SimpleNetworkWrapper ConfigApiNetwork = NetworkRegistry.INSTANCE.newSimpleChannel("N2Core");
	private static int count = 0;

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessageHandler(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		ConfigApiNetwork.registerMessage(messageHandler, requestMessageType, count++, side);
	}
	
	public static <REQ extends IMessage, REPLY extends IMessage> void registerDoubleSidedMessageHandler(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType)
	{
		int c = count++;
		ConfigApiNetwork.registerMessage(messageHandler, requestMessageType, c, Side.CLIENT);
		ConfigApiNetwork.registerMessage(messageHandler, requestMessageType, c, Side.SERVER);
	}
	
	public static void sendPacketToPlayer(N2Packet packet, EntityPlayerMP player)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer() && checkServer())
			ConfigApiNetwork.sendTo(new N2MessageHandler().bindPacket(packet), player);
	}
	
	public static void sendPacketsToPlayer(List<N2Packet> packets, EntityPlayerMP player)
	{
		for (int i = 0; i < packets.size(); i++)
			sendPacketToPlayer(packets.get(i), player);
	}
	
	public static void sendPacketToAllPlayers(N2Packet packet)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer() && checkServer())
			ConfigApiNetwork.sendToAll(new N2MessageHandler().bindPacket(packet));
	}

	public static void sendPacketsToAllPlayers(List<N2Packet> packets)
	{
		for (int i = 0; i < packets.size(); i++)
			sendPacketToAllPlayers(packets.get(i));
	}
	
	public static void sendPacketToServer(N2Packet packet)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient() && checkServer())
			ConfigApiNetwork.sendToServer(new N2MessageHandler().bindPacket(packet));
	}
	
	public static void sendPacketsToServer(List<N2Packet> packets)
	{
		for (int i = 0; i < packets.size(); i++)
			sendPacketToServer(packets.get(i));
	}
	
	public static void sendPacketToPlayersAround(N2Packet packet, TargetPoint point)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer() && checkServer())
			ConfigApiNetwork.sendToAllAround(new N2MessageHandler().bindPacket(packet), point);
	}
	
	public static void sendPacketsToPlayersAround(List<N2Packet> packets, TargetPoint point)
	{
		for (int i = 0; i < packets.size(); i++)
			sendPacketToPlayersAround(packets.get(i), point);
	}
	
	public static void sendPacketToPlayersInDimension(N2Packet packet, int dimensionID)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer() && checkServer())
			ConfigApiNetwork.sendToDimension(new N2MessageHandler().bindPacket(packet), dimensionID);
	}
	
	public static void sendPacketsToPlayersInDimension(List<N2Packet> packets, int dimensionID)
	{
		for (int i = 0; i < packets.size(); i++)
			sendPacketToPlayersInDimension(packets.get(i), dimensionID);
	}
	
	private static boolean checkServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance() != null &&
				FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager() != null &&
				FMLCommonHandler.instance().getMinecraftServerInstance().isServerRunning();
	}
}
