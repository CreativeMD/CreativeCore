package com.creativemd.creativecore.common.multiblock.example;

import com.creativemd.creativecore.common.multiblock.IMultiBlock;
import com.creativemd.creativecore.common.multiblock.MultiBlockStructure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityExampleSturcture extends TileEntity implements IMultiBlock{
	
	public MultiBlockStructure multiblock = null;
	public boolean ismaster = false;
	
	/**Use block event for**/
	public void onNeighbourChanged()
	{
		multiblock.caculateUpdate(this);
	}
	
	@Override
	public void updateEntity()
	{
		if(ismaster)
			multiblock.updateTick();
	}
	
	public void createMultiBlock()
	{
		//Create your MultiBlock
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
    {
		super.readFromNBT(nbt);
		ismaster = nbt.getBoolean("master");
		if(ismaster)
		{
			createMultiBlock();
			multiblock.readFromNBT(nbt);
			multiblock.caculateUpdate(this);
		}
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
    	nbt.setBoolean("master", ismaster);
    	if(ismaster)
    	{
    		multiblock.writeToNBT(nbt);
    	}
    }

	@Override
	public MultiBlockStructure getStructure() {
		return multiblock;
	}

	@Override
	public void setStructure(MultiBlockStructure structure) {
		multiblock = structure;
	}

	@Override
	public void setMaster(boolean isMaster) {
		this.ismaster = isMaster;
	}
	
}
