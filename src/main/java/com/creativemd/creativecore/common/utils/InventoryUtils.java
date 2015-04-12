package com.creativemd.creativecore.common.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InventoryUtils {
	
	public static void saveInventory(ItemStack[] inventory, NBTTagCompound nbt)
	{
		NBTTagCompound nbtInv = nbt.getCompoundTag("inventory");
		for (int i = 0; i < inventory.length; i++) {
			NBTTagCompound newNBT = new NBTTagCompound();
			if(inventory[i] != null)
				inventory[i].writeToNBT(newNBT);
			nbtInv.setTag("slot" + i, newNBT);
		}
		nbtInv.setInteger("size", inventory.length);
		nbt.setTag("inventory", nbtInv);
	}
	
	public static ItemStack[] loadInventory(NBTTagCompound nbt)
	{
		ItemStack[]  inventory = new ItemStack[nbt.getCompoundTag("inventory").getInteger("size")];
		for (int i = 0; i < inventory.length; i++) {
			inventory[i] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inventory").getCompoundTag("slot" + i));
		}
		return inventory;
	}
	
}
