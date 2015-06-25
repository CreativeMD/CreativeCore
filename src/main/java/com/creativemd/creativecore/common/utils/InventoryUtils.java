package com.creativemd.creativecore.common.utils;

import net.minecraft.inventory.IInventory;
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
	
	public static boolean isItemStackEqual(ItemStack stack, ItemStack stack2)
	{
		if(stack != null && stack2 != null && stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage())
		{
			if(!stack.hasTagCompound() && !stack2.hasTagCompound())
				return true;
			else if(stack.hasTagCompound() != stack2.hasTagCompound())
				return false;
			else if(stack.stackTagCompound.equals(stack2.stackTagCompound))
				return true;
				
		}
		return false;
	}
	
	public static boolean consumeItemStack(IInventory inventory, ItemStack stack)
	{
		if(getAmount(inventory, stack) >= stack.stackSize)
		{
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if(isItemStackEqual(inventory.getStackInSlot(i), stack)){
					int amount = Math.min(stack.stackSize, inventory.getStackInSlot(i).stackSize);
					if(amount > 0)
					{
						inventory.getStackInSlot(i).stackSize -= amount;
						if(inventory.getStackInSlot(i).stackSize <= 0)
							inventory.setInventorySlotContents(i, null);
						stack.stackSize -= amount;
					}
					if(stack.stackSize <= 0)
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean addItemStackToInventory(IInventory inventory, ItemStack stack)
	{
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if(inventory.getStackInSlot(i) == null)
			{
				inventory.setInventorySlotContents(i, stack);
				return true;
			}
			else if(isItemStackEqual(inventory.getStackInSlot(i), stack)){
				int amount = Math.min(64-inventory.getStackInSlot(i).stackSize, stack.stackSize);
				if(amount > 0)
				{
					ItemStack newStack = stack.copy();
					newStack.stackSize = inventory.getStackInSlot(i).stackSize+amount;
					inventory.setInventorySlotContents(i, newStack);
					
					stack.stackSize -= amount;
					if(stack.stackSize <= 0)
						return true;
				}
			}
			
		}
		return false;
	}
	
	public static int getAmount(IInventory inventory, ItemStack stack)
	{
		int amount = 0;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if(isItemStackEqual(inventory.getStackInSlot(i), stack)){
				amount += inventory.getStackInSlot(i).stackSize;
			}
		}
		return amount;
	}
	
}
