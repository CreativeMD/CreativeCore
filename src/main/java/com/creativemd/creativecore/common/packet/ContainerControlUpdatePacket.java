package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.container.SubContainer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerControlUpdatePacket extends CreativeCorePacket{
	
	public NBTTagCompound value;
	public int id;
	
	public ContainerControlUpdatePacket()
	{
		super();
	}
	
	public ContainerControlUpdatePacket(int id, NBTTagCompound value)
	{
		super();
		this.value = value;
		this.id = id;
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		ByteBufUtils.writeTag(bytes, value);
		bytes.writeInt(id);
	}
	
	@Override
	public void readBytes(ByteBuf bytes) {
		value = ByteBufUtils.readTag(bytes);
		id = bytes.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		if(player.openContainer instanceof ContainerSub)
		{
			if(((ContainerSub) player.openContainer).controls.size() > id)
				((ContainerSub) player.openContainer).controls.get(id).readFromNBT(value);
		}
	}

	@Override
	public void executeServer(EntityPlayer player) {
		if(player.openContainer instanceof ContainerSub)
		{
			if(((ContainerSub) player.openContainer).controls.size() > id)
				((ContainerSub) player.openContainer).controls.get(id).readFromNBT(value);
		}
	}

}
