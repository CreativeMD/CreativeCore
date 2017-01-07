package com.creativemd.creativecore.common.packet;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CreativeMessageHandler implements IMessage{
	
	public CreativeMessageHandler()
	{
		
	}
	
	public boolean isLast = true;
	public UUID uuid;
	public CreativeCorePacket packet = null;
	public MessageType type;
	public EntityPlayer player;
	public int amount;
	public ByteBuf content;
	
	public CreativeMessageHandler(CreativeCorePacket packet, MessageType type, EntityPlayer player)
	{
		this.packet = packet;
		this.type = type;
		this.player = player;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		isLast = buf.readBoolean();
		
		String id = CreativeCorePacket.readString(buf);
		Class PacketClass = CreativeCorePacket.getClassByID(id);
		packet = null;
		try {
			packet = (CreativeCorePacket) PacketClass.getConstructor().newInstance();
		} catch (Exception e) {
			System.out.println("Invalid packet id=" + id);
		}
		
		if(isLast)
			if(packet != null)
				packet.readBytes(buf);
		else{
			amount = buf.readInt();
			uuid = UUID.fromString(CreativeCorePacket.readString(buf));
			int length = buf.readInt();
			content = ByteBufAllocator.DEFAULT.buffer();
			buf.readBytes(content, length);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {		
		content = ByteBufAllocator.DEFAULT.buffer();
		packet.writeBytes(content);
		
		if(CreativeCorePacket.maxPacketSize > content.capacity())
		{
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, CreativeCorePacket.getIDByClass(packet));
			buf.writeBytes(content);
		}
		else
		{
			//CREATE SPLITTED MESSAGES
			amount = (int) Math.ceil(content.capacity() / CreativeCorePacket.maxPacketSize);
			
			uuid = UUID.randomUUID();
			String id = CreativeCorePacket.getIDByClass(packet);
			for (int i = 0; i < amount; i++) {
				int length = Math.min(CreativeCorePacket.maxPacketSize, content.capacity() - i*CreativeCorePacket.maxPacketSize);
				if(i == 0)
				{
					buf.writeBoolean(false);
					CreativeCorePacket.writeString(buf, id);
					buf.writeInt(amount);
					CreativeCorePacket.writeString(buf, uuid.toString());
					buf.writeInt(length);
					buf.writeBytes(buf, 0, length);
				}else{
					CreativeSplittedMessageHandler splitted = new CreativeSplittedMessageHandler(i == amount-1, id, uuid, content, i*CreativeCorePacket.maxPacketSize, length);
				}
			}
		}
	}
	
	public static enum MessageType {
		
		ToServer,
		ToPlayer,
		ToAllPlayer;
		
	}

}
