package com.creativemd.creativecore.common.recipe.entry;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class RecipeEntry {
	
	public abstract boolean isEntry(ItemStack stack);
	
	public abstract int getStackSize(ItemStack stack);
	
	public void consumeItemStack(int amount, int index, IInventory inventory) {
		ItemStack stack = inventory.getStackInSlot(index);
		stack.shrink(amount * getStackSize(stack));
		if (stack.isEmpty())
			inventory.setInventorySlotContents(index, null);
	}
	
	public static boolean isObject(ItemStack stack, Object object) {
		if (stack == null)
			return object == null;
		if (object instanceof Block) {
			return Block.getBlockFromItem(stack.getItem()) == object;
		} else if (object instanceof Item) {
			return stack.getItem() == object;
		} else if (object instanceof ItemStack) {
			ItemStack input = (ItemStack) object;
			if (stack.getItem() != input.getItem())
				return false;
			if (stack.getMetadata() != input.getMetadata())
				return false;
			if (stack.getTagCompound() != input.getTagCompound())
				if (stack.hasTagCompound() && input.hasTagCompound())
					return stack.getTagCompound().equals(input.getTagCompound());
				else
					return false;
			else
				return true;
		} else if (object instanceof String) {
			int[] ores = OreDictionary.getOreIDs(stack);
			int id = OreDictionary.getOreID((String) object);
			for (int i = 0; i < ores.length; i++) {
				if (ores[i] == id)
					return true;
			}
			return false;
		} else {
			return stack == null;
		}
	}
}
