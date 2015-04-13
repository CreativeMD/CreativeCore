package com.creativemd.creativecore.common.multiblock;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class MultiBlockStructure {
	
	public static ArrayList<MultiBlockStructure> allstructures = new ArrayList<MultiBlockStructure>();
	
	public ConnectionStructure structure;
	
	public MultiBlockStructure(ConnectionStructure structure)
	{
		allstructures.add(this);
		this.structure = structure;
		this.structure.multiblock = this;
	}
	
	public boolean canConnectToBlock(World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity instanceof IMultiBlock)
		{
			if(((IMultiBlock) tileEntity).getStructure() == null || ((IMultiBlock) tileEntity).getStructure() == this)
				return canBlockBelongtoStructure(world, x, y, z);
		}
		return false;
	}
	
	protected abstract boolean canBlockBelongtoStructure(World world, int x, int y, int z);
	
	public ArrayList<TileEntity> connections = new ArrayList<TileEntity>();
	public TileEntity masterBlock = null;
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		
	}
	
	public void readFromNBT(NBTTagCompound nbt)
    {
		
    }
	
	public abstract void updateTick();
	
	public void caculateUpdate(TileEntity tileEntity)
	{
		ArrayList<TileEntity> newconnections = structure.caculateAllConnectedBlocks(tileEntity);
		if(newconnections.size() > structure.getMaxConnections())
			valid = false;
		else{
			for (int i = 0; i < connections.size(); i++) {
				if(((IMultiBlock)connections.get(i)).getStructure() == this && !newconnections.contains(connections))
					((IMultiBlock)connections.get(i)).setStructure(null);
			}
			connections = newconnections;
			if(!structure.isStructureValid(connections))
				valid = false;
		}
		if(valid)
		{
			masterBlock = structure.caculateMasterBlock(connections);
			for (int i = 0; i < connections.size(); i++) {
				((IMultiBlock) connections.get(i)).setMaster(masterBlock == connections.get(i));
			}
		}
	}
	
	private boolean valid;

	public boolean isValid() {
		return valid;
	}
	
}
