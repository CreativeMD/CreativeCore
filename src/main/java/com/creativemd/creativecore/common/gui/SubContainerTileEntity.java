package com.creativemd.creativecore.common.gui;

import net.minecraft.tileentity.TileEntity;

import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.tileentity.TileEntityCreative;

public abstract class SubContainerTileEntity extends SubContainer {
	
	public TileEntity tileEntity;
	
	public SubContainerTileEntity(TileEntity tileEntity)
	{
		this.tileEntity = tileEntity;
	}
	
	public void sendUpdate()
	{
		if(tileEntity instanceof TileEntityCreative)
			((TileEntityCreative)tileEntity).updateBlock();
		else
			tileEntity.getWorldObj().markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}
}
