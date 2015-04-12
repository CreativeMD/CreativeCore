package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.container.ContainerSub;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class GuiPacket extends CreativeCorePacket{
	
	public int control;
	public String value;
	
	public GuiPacket()
	{
		
	}
	
	public GuiPacket(int control, String value)
	{
		this.control = control;
		this.value = value;
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		bytes.writeInt(control);
		ByteBufUtils.writeUTF8String(bytes, value);
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		control = bytes.readInt();
		value = ByteBufUtils.readUTF8String(bytes);
	}

	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		if(player.openContainer instanceof ContainerSub)
			((ContainerSub) player.openContainer).subContainer.onGuiPacket(control, value, player);
	}

}
