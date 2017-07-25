package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;
import java.util.Arrays;

import com.creativemd.creativecore.common.utils.stack.InfoStack;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.loot.functions.SetCount;
import scala.collection.immutable.Stack;

public class InventoryUtils {
	
	public static NBTTagCompound saveInventoryBasic(IInventory basic)
	{
		ItemStack[] stacks = new ItemStack[basic.getSizeInventory()];
		for (int i = 0; i < stacks.length; i++) {
			stacks[i] = basic.getStackInSlot(i);
		}
		return saveInventory(stacks);
	}
	
	public static NBTTagCompound saveInventory(ItemStack[] inventory)
	{
		NBTTagCompound nbtInv = new NBTTagCompound();
		for (int i = 0; i < inventory.length; i++) {
			NBTTagCompound newNBT = new NBTTagCompound();
			if(inventory[i] != null)
				inventory[i].writeToNBT(newNBT);
			nbtInv.setTag("slot" + i, newNBT);
		}
		nbtInv.setInteger("size", inventory.length);
		return nbtInv;
	}
	
	public static ItemStack[] loadInventory(NBTTagCompound nbt)
	{
		ItemStack[] inventory = new ItemStack[nbt.getInteger("size")];
		for (int i = 0; i < inventory.length; i++) {
			inventory[i] = new ItemStack(nbt.getCompoundTag("slot" + i));
		}
		return inventory;
	}
	
	public static InventoryBasic loadInventoryBasic(NBTTagCompound nbt)
	{
		return loadInventoryBasic(nbt, nbt.getInteger("size"));
	}
	
	public static InventoryBasic loadInventoryBasic(NBTTagCompound nbt, int length)
	{
		
		InventoryBasic basic = new InventoryBasic("basic", false, length);
		if(nbt != null)
		{
			ItemStack[] stacks = loadInventory(nbt);
			for (int i = 0; i < stacks.length; i++) {
				if(length > i)
					basic.setInventorySlotContents(i, stacks[i]);
			}
		}
		return basic;
	}
	
	public static boolean isItemStackEqual(ItemStack stackA, ItemStack stackB)
	{
		return stackA == stackB ? true : (!stackA.isEmpty() && !stackB.isEmpty() ? stackA.isItemEqual(stackB) : false);
	}
	
	public static boolean consumeItemStack(IInventory inventory, ItemStack stack)
	{
		if(getAmount(inventory, stack) >= stack.getCount())
		{
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if(isItemStackEqual(inventory.getStackInSlot(i), stack)){
					int amount = Math.min(stack.getCount(), inventory.getStackInSlot(i).getCount());
					if(amount > 0)
					{
						inventory.getStackInSlot(i).shrink(amount);
						if(inventory.getStackInSlot(i).isEmpty())
							inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						stack.shrink(amount);
					}
					if(stack.isEmpty())
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack)
	{
		if(stack.isEmpty())
			return true;
		for (int i = 0; i < inventory.length; i++) {
			if(isItemStackEqual(inventory[i], stack)){
				int amount = Math.min(stack.getMaxStackSize()-inventory[i].getCount(), stack.getCount());
				if(amount > 0)
				{
					ItemStack newStack = stack.copy();
					newStack.setCount(inventory[i].getCount()+amount);
					inventory[i] = newStack;
					
					stack.shrink(amount);
					if(stack.isEmpty())
						return true;
				}
			}
			
		}
		for (int i = 0; i < inventory.length; i++) {
			if(inventory[i] == null || inventory[i].isEmpty())
			{
				inventory[i] = stack;
				return true;
			}
		}
		return false;
	}
	
	public static boolean addItemStackToInventory(IInventory inventory, ItemStack stack)
	{
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if(isItemStackEqual(inventory.getStackInSlot(i), stack)){
				int amount = Math.min(stack.getMaxStackSize()-inventory.getStackInSlot(i).getCount(), stack.getCount());
				if(amount > 0)
				{
					ItemStack newStack = stack.copy();
					newStack.setCount(inventory.getStackInSlot(i).getCount()+amount);
					inventory.setInventorySlotContents(i, newStack);
					
					stack.shrink(amount);
					if(stack.isEmpty())
						return true;
				}
			}
			
		}
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if(inventory.getStackInSlot(i).isEmpty())
			{
				inventory.setInventorySlotContents(i, stack);
				return true;
			}
		}
		return false;
	}
	
	public static int getAmount(IInventory inventory, ItemStack stack)
	{
		int amount = 0;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if(isItemStackEqual(inventory.getStackInSlot(i), stack)){
				amount += inventory.getStackInSlot(i).getCount();
			}
		}
		return amount;
	}
	
	public static void cleanInventory(IInventory inventory)
	{
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack != null && stack.isEmpty())
				inventory.setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}

	public static int consumeInfoStack(InfoStack info, IInventory inventory)
	{
		return consumeInfoStack(info, inventory, null);
	}
	
	public static int consumeInfoStack(InfoStack info, IInventory inventory, ArrayList<ItemStack> consumed)
	{
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		int stackSize = info.stackSize;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(!stack.isEmpty() && info.isInstanceIgnoreSize(stack))
			{
				
				int used = Math.min(stackSize, stack.getCount());
				stack.shrink(used);
				stackSize -= used;
				ItemStack stackCopy = stack.copy();
				stackCopy.setCount(used);
				stacks.add(stackCopy);
				if(stackSize <= 0)
					break;
			}
		}
		if(consumed != null)
			consumed.addAll(stacks);
		return stackSize;
	}
	
}
