package com.creativemd.creativecore.common.multiblock;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class MultiBlockStructure {
	
	public static ArrayList<MultiBlockStructure> allstructures = new ArrayList<MultiBlockStructure>();
	
	public MultiBlockStructure()
	{
		allstructures.add(this);
	}
	
	public abstract int getMaxConnections();
	
	public abstract boolean canBlockBelongtoStructure(World world, int x, int y, int z);
	
	public abstract ConnectionStructure getConnectionStructure();
	
	public ArrayList<TileEntity> connections = new ArrayList<TileEntity>();
	public TileEntity masterBlock = null;
	
	public abstract void updateTick();
	
	public void caculateUpdate()
	{
		ArrayList<TileEntity> newconnections = getConnectionStructure().caculateAllConnectedBlocks();
		for (int i = 0; i < connections.size(); i++) {
			if(((IMultiBlock)connections.get(i)).getStructure() == this && !newconnections.contains(connections))
				((IMultiBlock)connections.get(i)).setStructure(null);
		}
		connections = newconnections;
	}
	
	private boolean valid;

	public boolean isValid() {
		return valid;
	}
	
}
