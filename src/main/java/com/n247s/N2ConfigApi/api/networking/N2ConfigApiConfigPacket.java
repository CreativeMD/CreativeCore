package com.n247s.N2ConfigApi.api.networking;

import io.netty.buffer.ByteBuf;

import org.apache.logging.log4j.Logger;

import com.n247s.N2ConfigApi.api.N2ConfigApi;
import com.n247s.n2core.networking.N2MessageHandler;
import com.n247s.n2core.networking.N2Packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * @author N247S
 * An ingame ConfigFile Manager<br>
 * <br>
 * This is a Class to handle synchronization internally. There is no need of using any method or field inside this Class.
 */
public class N2ConfigApiConfigPacket extends N2Packet
{
	private static final Logger log = N2ConfigApi.log;
	private ByteBuf byteBuff;
	
	public N2ConfigApiConfigPacket(ByteBuf buff)
	{
		super(buff);
		if(buff != null)
		{
			this.byteBuff = buff;
		}
	}
	
	@Override
	protected IMessage onMessage(N2MessageHandler message, MessageContext ctx)
	{
		if(ctx.side.isServer())
			N2ConfigApiMessageHandler.processPackage(byteBuff, ctx.getServerHandler().playerEntity);
		N2ConfigApiMessageHandler.processPackage(byteBuff, null);
		return null;
	}

	@Override
	protected String getID()
	{
		return "N2ConfigApi";
	}

	@Override
	protected void readBytes(ByteBuf buf)
	{
		byteBuff.capacity(buf.capacity());
		byteBuff.setBytes(0, buf);
	}

	@Override
	protected void writeBytes(ByteBuf buf)
	{
		int capacity = buf.writerIndex() - buf.readerIndex();
		buf.capacity(capacity + byteBuff.capacity());
		buf.setBytes(buf.writerIndex(), this.byteBuff);
		buf.writerIndex(buf.capacity());
	}
	
	public ByteBuf getByteBuf()
	{
		return this.byteBuff;
	}
}
