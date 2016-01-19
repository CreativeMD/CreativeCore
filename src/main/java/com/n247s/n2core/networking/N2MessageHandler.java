package com.n247s.n2core.networking;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.apache.logging.log4j.Logger;

import com.n247s.n2core.N2Core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class N2MessageHandler implements IMessage
{
	private static final Logger log = N2Core.logger;
	private N2Packet packet;
	
	public N2MessageHandler() {}
	
	protected N2MessageHandler bindPacket(N2Packet packet)
	{
		if(packet != null)
			this.packet = packet;
		else log.catching(new IllegalArgumentException("packet can't be null!"));
		return this;
	}
	
	public IMessage onMessageReciev(N2MessageHandler message, MessageContext ctx)
	{
		return message.packet.onMessage(message, ctx);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		String id = ByteBufUtils.readUTF8String(buf);
		
		Class<? extends N2Packet> clazz = N2PacketHandler.getPacketClass(id);
		if(clazz != null)
		{
			try
			{
				Constructor<N2Packet> constructor = (Constructor<N2Packet>) clazz.getConstructor(ByteBuf.class);
				if(constructor != null)
					packet = constructor.newInstance(buf);
				else packet = clazz.newInstance();
				System.out.println("");
//				packet = clazz.newInstance();
//				packet.readBytes(buf);
			}
			catch(Exception e)
			{
				log.catching(e);
				return;
			}
		}
		else log.catching(new Exception("Couldn't find PacketType " + id));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, N2PacketHandler.getPacketID(packet.getClass()));
		packet.writeBytes(buf);
	}
}
