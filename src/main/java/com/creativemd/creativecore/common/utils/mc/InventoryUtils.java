package com.creativemd.creativecore.common.utils.mc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.creativemd.creativecore.common.utils.stack.InfoStack;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InventoryUtils {
	
	public static NBTTagCompound saveInventoryBasic(IInventory basic) {
		ItemStack[] stacks = new ItemStack[basic.getSizeInventory()];
		for (int i = 0; i < stacks.length; i++) {
			stacks[i] = basic.getStackInSlot(i);
		}
		return saveInventory(stacks);
	}
	
	public static NBTTagCompound saveInventory(ItemStack[] inventory) {
		NBTTagCompound nbtInv = new NBTTagCompound();
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i].isEmpty())
				continue;
			NBTTagCompound newNBT = new NBTTagCompound();
			inventory[i].writeToNBT(newNBT);
			nbtInv.setTag("slot" + i, newNBT);
		}
		nbtInv.setInteger("size", inventory.length);
		return nbtInv;
	}
	
	public static ItemStack[] loadInventory(NBTTagCompound nbt) {
		ItemStack[] inventory = new ItemStack[nbt.getInteger("size")];
		for (int i = 0; i < inventory.length; i++) {
			if (nbt.hasKey("slot" + i))
				inventory[i] = new ItemStack(nbt.getCompoundTag("slot" + i));
			else
				inventory[i] = ItemStack.EMPTY;
		}
		return inventory;
	}
	
	public static InventoryBasic loadInventoryBasic(NBTTagCompound nbt) {
		return loadInventoryBasic(nbt, nbt.getInteger("size"));
	}
	
	public static InventoryBasic loadInventoryBasic(NBTTagCompound nbt, int length) {
		
		InventoryBasic basic = new InventoryBasic("basic", false, length);
		if (nbt != null) {
			ItemStack[] stacks = loadInventory(nbt);
			for (int i = 0; i < stacks.length; i++) {
				if (length > i)
					basic.setInventorySlotContents(i, stacks[i]);
			}
		}
		return basic;
	}
	
	public static boolean isItemStackEqual(ItemStack stackA, ItemStack stackB) {
		
		if (stackA.isEmpty() && stackB.isEmpty())
			return true;
		
		if (stackA.isEmpty() || stackB.isEmpty())
			return false;
		
		if (stackA.getItem() != stackB.getItem())
			return false;
		
		if (stackA.isItemEqual(stackB))
			return false;
		
		if (!stackA.hasTagCompound() && stackB.hasTagCompound())
			return false;
		
		return (!stackA.hasTagCompound() || stackA.getTagCompound().equals(stackB.getTagCompound())) && stackA.areCapsCompatible(stackB);
	}
	
	public static boolean consumeItemStack(IInventory inventory, ItemStack stack) {
		if (getAmount(inventory, stack) >= stack.getCount()) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (isItemStackEqual(inventory.getStackInSlot(i), stack)) {
					int amount = Math.min(stack.getCount(), inventory.getStackInSlot(i).getCount());
					if (amount > 0) {
						inventory.getStackInSlot(i).shrink(amount);
						if (inventory.getStackInSlot(i).isEmpty())
							inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						stack.shrink(amount);
					}
					if (stack.isEmpty())
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack) {
		if (stack.isEmpty())
			return true;
		for (int i = 0; i < inventory.length; i++) {
			if (isItemStackEqual(inventory[i], stack)) {
				int amount = Math.min(stack.getMaxStackSize() - inventory[i].getCount(), stack.getCount());
				if (amount > 0) {
					ItemStack newStack = stack.copy();
					newStack.setCount(inventory[i].getCount() + amount);
					inventory[i] = newStack;
					
					stack.shrink(amount);
					if (stack.isEmpty())
						return true;
				}
			}
			
		}
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null || inventory[i].isEmpty()) {
				inventory[i] = stack;
				return true;
			}
		}
		return false;
	}
	
	public static boolean addItemStackToInventory(IInventory inventory, ItemStack stack) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (isItemStackEqual(inventory.getStackInSlot(i), stack)) {
				int amount = Math.min(stack.getMaxStackSize() - inventory.getStackInSlot(i).getCount(), stack.getCount());
				if (amount > 0) {
					ItemStack newStack = stack.copy();
					newStack.setCount(inventory.getStackInSlot(i).getCount() + amount);
					inventory.setInventorySlotContents(i, newStack);
					
					stack.shrink(amount);
					if (stack.isEmpty())
						return true;
				}
			}
			
		}
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				inventory.setInventorySlotContents(i, stack);
				return true;
			}
		}
		return false;
	}
	
	public static int getAmount(IInventory inventory, ItemStack stack) {
		int amount = 0;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (isItemStackEqual(inventory.getStackInSlot(i), stack)) {
				amount += inventory.getStackInSlot(i).getCount();
			}
		}
		return amount;
	}
	
	public static void cleanInventory(IInventory inventory) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.isEmpty())
				inventory.setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}
	
	public static int consumeInfoStack(InfoStack info, IInventory inventory) {
		return consumeInfoStack(info, inventory, null);
	}
	
	public static int consumeInfoStack(InfoStack info, IInventory inventory, ArrayList<ItemStack> consumed) {
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		int stackSize = info.stackSize;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty() && info.isInstanceIgnoreSize(stack)) {
				
				int used = Math.min(stackSize, stack.getCount());
				stack.shrink(used);
				stackSize -= used;
				ItemStack stackCopy = stack.copy();
				stackCopy.setCount(used);
				stacks.add(stackCopy);
				if (stackSize <= 0)
					break;
			}
		}
		if (consumed != null)
			consumed.addAll(stacks);
		return stackSize;
	}
	
	public static void sortInventory(IInventory inventory, boolean alphabetical) {
		List<ItemStack> sorting = new ArrayList<>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty())
				sorting.add(stack);
		}
		
		if (alphabetical)
			Collections.sort(sorting, new Comparator<ItemStack>() {
				
				@Override
				public int compare(ItemStack arg0, ItemStack arg1) {
					return arg0.getDisplayName().compareToIgnoreCase(arg1.getDisplayName());
				}
				
			});
		else
			Collections.sort(sorting, new Comparator<ItemStack>() {
				
				@Override
				public int compare(ItemStack arg0, ItemStack arg1) {
					int id0 = Item.getIdFromItem(arg0.getItem());
					int id1 = Item.getIdFromItem(arg1.getItem());
					if (id0 < id1)
						return -1;
					if (id0 > id1)
						return 1;
					
					if (arg0.getMetadata() < arg1.getMetadata())
						return -1;
					if (arg0.getMetadata() > arg1.getMetadata())
						return 1;
					
					return arg0.getDisplayName().compareToIgnoreCase(arg1.getDisplayName());
				}
				
			});
		
		int maxStackSize = inventory.getInventoryStackLimit();
		for (int i = 0; i < sorting.size() - 1; i++) {
			ItemStack stack0 = sorting.get(i);
			ItemStack stack1 = sorting.get(i + 1);
			
			if (isItemStackEqual(stack0, stack1)) {
				int maxStack = Math.min(maxStackSize, stack0.getMaxStackSize());
				int use = Math.min(maxStack - stack0.getCount(), stack1.getCount());
				if (use > 0) {
					stack0.grow(use);
					stack1.shrink(use);
					if (stack1.isEmpty())
						sorting.remove(i + 1);
					i--;
				}
			}
		}
		
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			inventory.setInventorySlotContents(i, i < sorting.size() ? sorting.get(i) : ItemStack.EMPTY);
		}
		
	}
	
}
