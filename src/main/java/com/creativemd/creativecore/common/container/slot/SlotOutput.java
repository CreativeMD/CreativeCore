package com.creativemd.creativecore.common.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutput extends Slot{
	
	public ItemStack stack;
	
	public SlotOutput(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public void onSlotChanged()
    {
		super.onSlotChanged();
		if(getHasStack())
			stack = getStack().copy();
		else
			stack = null;
    }
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
        return false;
    }
	
}
