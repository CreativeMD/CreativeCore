package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;
import java.util.Arrays;

import com.creativemd.creativecore.common.utils.stack.StackInfo;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import scala.collection.immutable.Stack;

public class InventoryUtils {
	
	public static NBTTagCompound saveInventoryBasic(InventoryBasic basic)
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
			inventory[i] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("slot" + i));
		}
		return inventory;
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
	
	public static boolean isItemStackEqual(ItemStack stack, ItemStack stack2)
	{
		if(stack != null && stack2 != null && stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage())
		{
			if(!stack.hasTagCompound() && !stack2.hasTagCompound())
				return true;
			else if(stack.hasTagCompound() != stack2.hasTagCompound())
				return false;
			else if(stack.getTagCompound().equals(stack2.getTagCompound()))
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
	
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack)
	{
		if(stack.stackSize <= 0)
			return true;
		for (int i = 0; i < inventory.length; i++) {
			if(isItemStackEqual(inventory[i], stack)){
				int amount = Math.min(64-inventory[i].stackSize, stack.stackSize);
				if(amount > 0)
				{
					ItemStack newStack = stack.copy();
					newStack.stackSize = inventory[i].stackSize+amount;
					inventory[i] = newStack;
					
					stack.stackSize -= amount;
					if(stack.stackSize <= 0)
						return true;
				}
			}
			
		}
		for (int i = 0; i < inventory.length; i++) {
			if(inventory[i] == null)
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
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if(inventory.getStackInSlot(i) == null)
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
				amount += inventory.getStackInSlot(i).stackSize;
			}
		}
		return amount;
	}
	
	public static void cleanInventory(IInventory inventory)
	{
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack != null && stack.stackSize == 0)
				inventory.setInventorySlotContents(i, null);
		}
	}

	public static int consumeStackInfo(StackInfo info, IInventory inventory)
	{
		return consumeStackInfo(info, inventory, null);
	}
	
	public static int consumeStackInfo(StackInfo info, IInventory inventory, ArrayList<ItemStack> consumed)
	{
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		int stackSize = info.stackSize;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack != null && info.isInstanceIgnoreSize(stack))
			{
				
				int used = Math.min(stackSize, stack.stackSize);
				stack.stackSize -= used;
				stackSize -= used;
				ItemStack stackCopy = stack.copy();
				stackCopy.stackSize = used;
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
