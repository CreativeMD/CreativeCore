package com.creativemd.creativecore.common.packet.gui;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.gui.mc.ContainerSub;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiUpdatePacket extends CreativeCorePacket {
	
	public NBTTagCompound value;
	public boolean isOpening;
	public int layer;
	
	public GuiUpdatePacket() {
		super();
	}
	
	public GuiUpdatePacket(NBTTagCompound value, boolean isOpening, int layer) {
		this.value = value;
		this.isOpening = isOpening;
		this.layer = layer;
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		ByteBufUtils.writeTag(bytes, value);
		bytes.writeBoolean(isOpening);
		bytes.writeInt(layer);
	}
	
	@Override
	public void readBytes(ByteBuf bytes) {
		value = ByteBufUtils.readTag(bytes);
		isOpening = bytes.readBoolean();
		layer = bytes.readInt();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		if (player.openContainer instanceof ContainerSub)
			/*
			 * if(isOpening) ((ContainerSub)
			 * player.openContainer).gui.getLayers().get(layer).readFromOpeningNBT(value);
			 * else ((ContainerSub)
			 * player.openContainer).gui.getLayers().get(layer).readFromNBT(value);
			 */
			((ContainerSub) player.openContainer).layers.get(layer).receivePacket(value);
	}
	
	@Override
	public void executeServer(EntityPlayer player) {
		if (player.openContainer instanceof ContainerSub)
			((ContainerSub) player.openContainer).layers.get(layer).receivePacket(value);
	}
	
}
