package com.creativemd.creativecore.common.packet;

import java.util.UUID;

import com.creativemd.creativecore.common.packet.CreativeMessageHandler.MessageType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CreativeSplittedMessageHandler implements IMessage {
    
    public CreativeSplittedMessageHandler() {
        
    }
    
    public CreativeSplittedMessageHandler(boolean isLast, int packetId, UUID uuid, ByteBuf buffer, int index, int length) {
        this.isLast = isLast;
        this.buffer = buffer;
        this.uuid = uuid;
        this.index = index;
        this.length = length;
        this.packetId = packetId;
    }
    
    public boolean isLast;
    public int packetId;
    public UUID uuid;
    public ByteBuf buffer;
    public int index;
    public int length;
    public MessageType type;
    
    @Override
    public void fromBytes(ByteBuf buf) {
        isLast = buf.readBoolean();
        packetId = buf.readInt();
        uuid = UUID.fromString(CreativeCorePacket.readString(buf));
        buffer = ByteBufAllocator.DEFAULT.directBuffer();
        length = buf.readInt();
        byte[] data = new byte[length];
        buf.readBytes(data);
        buffer.writeBytes(data);
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isLast);
        buf.writeInt(packetId);
        CreativeCorePacket.writeString(buf, uuid.toString());
        buf.writeInt(length);
        buf.writeBytes(buffer, index, length);
    }
    
}
