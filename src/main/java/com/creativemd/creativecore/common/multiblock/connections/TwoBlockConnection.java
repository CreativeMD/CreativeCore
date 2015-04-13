package com.creativemd.creativecore.common.multiblock.connections;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

import com.creativemd.creativecore.common.multiblock.ConnectionStructure;
import com.creativemd.creativecore.common.utils.RotationUtils;

public class TwoBlockConnection extends ConnectionStructure{

	@Override
	public TileEntity caculateMasterBlock(ArrayList<TileEntity> blocks) {
		return getLowestPoint(blocks);
	}

	@Override
	public ArrayList<TileEntity> caculateAllConnectedBlocks(
			TileEntity tileEntity) {
		ArrayList<TileEntity> tiles = new ArrayList<TileEntity>();
		for (int i = 0; i < 6; i++) {
			ChunkCoordinates coord = new ChunkCoordinates(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			RotationUtils.applyDirection(direction, coord);
			if(multiblock.canConnectToBlock(tileEntity.getWorldObj(), coord.posX, coord.posY, coord.posZ))
				tiles.add(tileEntity);
		}
		return tiles;
	}

	@Override
	public boolean isStructureValid(ArrayList<TileEntity> blocks) {
		return true;
	}

	@Override
	public int getMaxConnections() {
		return 2;
	}

}
