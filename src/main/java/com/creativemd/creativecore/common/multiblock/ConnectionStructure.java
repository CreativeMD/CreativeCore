package com.creativemd.creativecore.common.multiblock;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

public abstract class ConnectionStructure {
	
	public MultiBlockStructure multiblock = null;
	
	public abstract TileEntity caculateMasterBlock(ArrayList<TileEntity> blocks);
	
	public abstract ArrayList<TileEntity> caculateAllConnectedBlocks(TileEntity tileEntity);
	
	public abstract boolean isStructureValid(ArrayList<TileEntity> blocks);
	
	public abstract int getMaxConnections();
	
	public static TileEntity getLowestPoint(ArrayList<TileEntity> blocks)
	{
		TileEntity lowest = null;
		int lowestPos = -1;
		for (int i = 0; i < blocks.size(); i++) {
			int pos = blocks.get(i).xCoord + blocks.get(i).yCoord + blocks.get(i).zCoord;
			if(lowest == null || lowestPos > pos)
			{
				lowest = blocks.get(i);
				lowestPos = pos;
			}else if(lowestPos == pos){
				boolean isLowest = false;
				if(blocks.get(i).xCoord > lowest.xCoord)
					isLowest = true;
				else if(blocks.get(i).xCoord == lowest.xCoord)
					if(blocks.get(i).yCoord > lowest.yCoord)
						isLowest = true;
					else if(blocks.get(i).yCoord == lowest.yCoord)
						if(blocks.get(i).zCoord > lowest.zCoord)
							isLowest = true;
				if(isLowest)
				{
					lowest = blocks.get(i);
					lowestPos = pos;
				}
			}
		}
		return lowest;
	}
}
