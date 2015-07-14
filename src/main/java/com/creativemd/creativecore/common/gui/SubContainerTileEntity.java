package com.creativemd.creativecore.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.TEContainerPacket;
import com.creativemd.creativecore.common.tileentity.TileEntityCreative;

public abstract class SubContainerTileEntity extends SubContainer {
	
	public TileEntity tileEntity;
	
	public boolean started = false;
	
	public SubContainerTileEntity(TileEntity tileEntity, EntityPlayer player)
	{
		super(player);
		this.tileEntity = tileEntity;
	}
	
	@Override
	public void writeOpeningNBT(NBTTagCompound nbt)
	{
		tileEntity.writeToNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		tileEntity.writeToNBT(nbt);
	}
}
