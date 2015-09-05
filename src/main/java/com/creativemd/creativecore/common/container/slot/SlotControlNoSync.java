package com.creativemd.creativecore.common.container.slot;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SlotControlNoSync extends SlotControl{

	public SlotControlNoSync(Slot slot) {
		super(slot);
	}
	
	@Override
	public void sendNBTUpdate(int type, NBTTagCompound nbt)
	{	
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void sendUpdate()
	{
		
	}

}
