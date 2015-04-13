package com.creativemd.creativecore.common.multiblock.structures;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.creativemd.creativecore.common.multiblock.ConnectionStructure;
import com.creativemd.creativecore.common.multiblock.MultiBlockStructure;

public class StructureTileEntity extends MultiBlockStructure{
	
	public Class<? extends TileEntity> tileEntityClass;
	
	public StructureTileEntity(Class<? extends TileEntity> tileEntityClass, ConnectionStructure structure)
	{
		super(structure);
		this.tileEntityClass = tileEntityClass;
	}
	
	@Override
	protected boolean canBlockBelongtoStructure(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity != null && tileEntity.getClass() == tileEntityClass)
			return true;
		return false;
	}

	@Override
	public void updateTick() {
		
	}

}
