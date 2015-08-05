package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.container.ContainerSub;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiLayerPacket extends CreativeCorePacket{
	
	public NBTTagCompound nbt;
	public int layer;
	
	public GuiLayerPacket(NBTTagCompound nbt, int layer)
	{
		this.nbt = nbt;
		this.layer = layer;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
		buf.writeInt(layer);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
		layer = buf.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		if(player.openContainer instanceof ContainerSub)
			((ContainerSub) player.openContainer).gui.getLayers().get(layer).openNewLayer(nbt);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		if(player.openContainer instanceof ContainerSub)
			((ContainerSub) player.openContainer).layers.get(layer).openNewLayer(nbt, true);		
	}

}
