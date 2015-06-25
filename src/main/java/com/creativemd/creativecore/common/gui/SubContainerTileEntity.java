package com.creativemd.creativecore.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
	
	public void onUpdate()
	{
		if(!started)
		{
			if(container.subContainer instanceof SubContainerTileEntity && player instanceof EntityPlayerMP)
				PacketHandler.sendPacketToPlayer(new TEContainerPacket(((SubContainerTileEntity)container.subContainer).tileEntity), (EntityPlayerMP)player);
			started = true;
		}
		super.onUpdate();		
	}
	
	public void sendUpdate()
	{
		if(tileEntity instanceof TileEntityCreative)
			((TileEntityCreative)tileEntity).updateBlock();
		else
			tileEntity.getWorldObj().markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}
}
