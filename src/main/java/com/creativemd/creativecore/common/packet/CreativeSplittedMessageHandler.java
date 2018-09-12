package com.creativemd.creativecore.common.packet;

import java.util.UUID;

import com.creativemd.creativecore.common.packet.CreativeMessageHandler.MessageType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CreativeSplittedMessageHandler implements IMessage {
	
	public CreativeSplittedMessageHandler() {
		
	}
	
	public CreativeSplittedMessageHandler(boolean isLast, String packetID, UUID uuid, ByteBuf buffer, int index, int length) {
		this.isLast = isLast;
		this.buffer = buffer;
		this.uuid = uuid;
		this.index = index;
		this.length = length;
		this.packetID = packetID;
	}
	
	public boolean isLast;
	public String packetID;
	public UUID uuid;
	public ByteBuf buffer;
	public int index;
	public int length;
	public MessageType type;
	
	@Override
	public void fromBytes(ByteBuf buf) {
		isLast = buf.readBoolean();
		packetID = CreativeCorePacket.readString(buf);
		uuid = UUID.fromString(CreativeCorePacket.readString(buf));
		buffer = ByteBufAllocator.DEFAULT.buffer();
		length = buf.readInt();
		byte[] data = new byte[length];
		buf.readBytes(data);
		buffer.writeBytes(data);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isLast);
		CreativeCorePacket.writeString(buf, packetID);
		CreativeCorePacket.writeString(buf, uuid.toString());
		buf.writeInt(length);
		buf.writeBytes(buffer, index, length);
	}
	
}
