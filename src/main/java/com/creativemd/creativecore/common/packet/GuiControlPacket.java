package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.container.ContainerSub;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GuiControlPacket extends GuiUpdatePacket{
	
	public int control;
	public int layer;
	
	public GuiControlPacket()
	{
		super();
	}
	
	public GuiControlPacket(int layer, int control, NBTTagCompound value)
	{
		super(value, false, layer);
		this.control = control;
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		super.writeBytes(bytes);
		bytes.writeInt(control);
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		super.readBytes(bytes);
		control = bytes.readInt();
	}

	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		if(player.openContainer instanceof ContainerSub)
			((ContainerSub) player.openContainer).layers.get(layer).onGuiPacket(control, value, player);
	}

}
