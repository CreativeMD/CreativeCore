package com.creativemd.creativecore.slots;

import com.creativemd.creativecore.common.utils.stack.StackInfo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInput extends Slot{
	
	public StackInfo input;
	
	public SlotInput(IInventory inventory, int index, int x, int y, StackInfo input) {
		super(inventory, index, x, y);
		this.input = input;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return true;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if(input == null)
			return false;
		return input.isInstanceIgnoreSize(stack);
	}

}
