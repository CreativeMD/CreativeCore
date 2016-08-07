package com.creativemd.creativecore.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotStackLimit extends Slot {
	
	public int stackLimit;

	public SlotStackLimit(IInventory inventoryIn, int index, int xPosition, int yPosition, int stackLimit) {
		super(inventoryIn, index, xPosition, yPosition);
		this.stackLimit = stackLimit;
	}
	
	public int getSlotStackLimit()
    {
        return Math.min(super.getSlotStackLimit(), stackLimit);
    }

}
