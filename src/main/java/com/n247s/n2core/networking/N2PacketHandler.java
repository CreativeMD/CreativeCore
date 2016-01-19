package com.n247s.n2core.networking;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;

import com.n247s.n2core.N2Core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class N2PacketHandler implements IMessageHandler<N2MessageHandler, IMessage>
{
	private static final HashMap<String, Class<? extends N2Packet>> packetList = new HashMap<String, Class<? extends N2Packet>>();
	private static final Logger log = N2Core.logger;
	
	public static void registerPacket(String id, Class<? extends N2Packet> packetClass)
	{
		if(!packetList.containsKey(id) && !packetList.containsValue(packetClass))
			packetList.put(id, packetClass);
	}
	
	public static Class<? extends N2Packet> getPacketClass(String id)
	{
		return packetList.get(id);
	}
	
	public static String getPacketID(Class<? extends N2Packet> clazz)
	{
		if(packetList.containsValue(clazz))
		{
			String id = null;
			Iterator<String> it = packetList.keySet().iterator();
			while(it.hasNext())
				if(packetList.get((id = it.next())).equals(clazz))
						return id;
		}
		return null;
	}
	
	@Override
	public IMessage onMessage(N2MessageHandler message, MessageContext ctx)
	{
		return message.onMessageReciev(message, ctx);
	}
}
