package com.creativemd.creativecore.common.multiblock;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

public abstract class ConnectionStructure {
	
	public abstract TileEntity caculateMasterBlock(ArrayList<TileEntity> blocks);
	
	public abstract ArrayList<TileEntity> caculateAllConnectedBlocks();
	
	public abstract boolean isStructureValid(ArrayList<TileEntity> blocks);
	
}
