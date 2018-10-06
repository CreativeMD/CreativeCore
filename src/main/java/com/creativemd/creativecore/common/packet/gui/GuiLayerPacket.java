package com.creativemd.creativecore.common.packet.gui;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.gui.mc.ContainerSub;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiLayerPacket extends CreativeCorePacket {
	
	public NBTTagCompound nbt;
	public int layer;
	public boolean closed;
	
	public GuiLayerPacket() {
		
	}
	
	public GuiLayerPacket(NBTTagCompound nbt, int layer, boolean closed) {
		this.nbt = nbt;
		this.layer = layer;
		this.closed = closed;
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
		buf.writeInt(layer);
		buf.writeBoolean(closed);
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
		layer = buf.readInt();
		closed = buf.readBoolean();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		if (player.openContainer instanceof ContainerSub) {
			if (closed)
				((ContainerSub) player.openContainer).gui.getLayers().get(layer).closeLayer(nbt, true);
			else
				((ContainerSub) player.openContainer).gui.getLayers().get(layer).openNewLayer(nbt, true);
			
		}
	}
	
	@Override
	public void executeServer(EntityPlayer player) {
		if (player.openContainer instanceof ContainerSub) {
			if (closed)
				((ContainerSub) player.openContainer).layers.get(layer).closeLayer(nbt, true);
			else
				((ContainerSub) player.openContainer).layers.get(layer).openNewLayer(nbt, true);
		}
	}
	
}
