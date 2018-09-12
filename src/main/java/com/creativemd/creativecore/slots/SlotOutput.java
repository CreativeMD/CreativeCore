package com.creativemd.creativecore.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutput extends Slot {
	
	public ItemStack stack;
	
	public SlotOutput(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player) {
		if (getHasStack())
			stack = getStack().copy();
		else
			stack = null;
		return true;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
	
}
