package com.creativemd.creativecore.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.creativemd.creativecore.common.container.ContainerSub;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiUpdatePacket extends CreativeCorePacket{
	
	public NBTTagCompound value;
	public boolean isOpening;
	
	public GuiUpdatePacket()
	{
		super();
	}
	
	public GuiUpdatePacket(NBTTagCompound value, boolean isOpening)
	{
		this.value = value;
		this.isOpening = isOpening;
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		ByteBufUtils.writeTag(bytes, value);
		bytes.writeBoolean(isOpening);
	}

	@Override
	public void readBytes(ByteBuf bytes) {
		value = ByteBufUtils.readTag(bytes);
		isOpening = bytes.readBoolean();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		if(player.openContainer instanceof ContainerSub)
			if(isOpening)
				((ContainerSub) player.openContainer).gui.gui.readFromOpeningNBT(value);
			else
				((ContainerSub) player.openContainer).gui.gui.readFromNBT(value);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		//if(player.openContainer instanceof ContainerSub)
			//((ContainerSub) player.openContainer).subContainer.onGuiPacket(control, value, player);
	}
	
}
