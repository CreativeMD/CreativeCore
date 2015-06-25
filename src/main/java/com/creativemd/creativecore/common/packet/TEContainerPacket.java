package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.gui.SubContainerTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TEContainerPacket extends CreativeCorePacket{
	
	public NBTTagCompound nbt;
	
	public TEContainerPacket()
	{
		
	}
	
	public TEContainerPacket(TileEntity tileEntity)
	{
		this.nbt = new NBTTagCompound();
		tileEntity.writeToNBT(nbt);
	}
		
	@Override
	public void writeBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		if(player.openContainer instanceof ContainerSub && ((ContainerSub)player.openContainer).subContainer instanceof SubContainerTileEntity)
		{
			((SubContainerTileEntity)((ContainerSub)player.openContainer).subContainer).tileEntity.readFromNBT(nbt);
		}
	}

	@Override
	public void executeServer(EntityPlayer player) {
		
	}

}
