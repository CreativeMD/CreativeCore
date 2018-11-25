package com.creativemd.creativecore.common.slots;

import com.creativemd.creativecore.common.utils.stack.InfoStack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInput extends Slot {
	
	public InfoStack input;
	
	public SlotInput(IInventory inventory, int index, int x, int y, InfoStack input) {
		super(inventory, index, x, y);
		this.input = input;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if (input == null)
			return false;
		return input.isInstanceIgnoreSize(stack);
	}
	
}
