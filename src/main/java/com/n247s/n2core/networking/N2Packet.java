package com.n247s.n2core.networking;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public abstract class N2Packet
{
	private String id;
	
	protected N2Packet(ByteBuf buff){}
	
	protected abstract IMessage onMessage(N2MessageHandler message, MessageContext ctx);
	
	protected abstract String getID();
	
	protected abstract void readBytes(ByteBuf buf);
	
	protected abstract void writeBytes(ByteBuf buf);
}
