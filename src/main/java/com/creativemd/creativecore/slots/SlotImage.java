package com.creativemd.creativecore.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotImage extends Slot{

	public SlotImage(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
		putStack(stack.copy());
        return false;
    }
	
	@Override
	public boolean canTakeStack(EntityPlayer player)
    {
		putStack(null);
        return false;
    }

}
