package com.creativemd.creativecore.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class CreativeTestPacket extends CreativeCorePacket {
	
	@Override
	public void writeBytes(ByteBuf buf) {
		byte[] bytes = new byte[3400000];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (Math.random()*Byte.MAX_VALUE);
		}
		buf.writeBytes(bytes);
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		byte[] bytes = new byte[3400000];
		buf.readBytes(bytes);
	}
	
	@Override
	public void executeServer(EntityPlayer player) {
		
	}
	
	@Override
	public void executeClient(EntityPlayer player) {
		
	}
	
}
