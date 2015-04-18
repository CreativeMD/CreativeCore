package com.creativemd.creativecore.common.recipe;

import com.creativemd.creativecore.common.recipe.entry.RecipeEntry;
import com.ibm.icu.impl.UBiDiProps;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CreativeRecipe {
	
	public ItemStack output;
	public Object[] input;
	public int width;
	public int height;
	
	public CreativeRecipe(ItemStack output, int width, int height, Object... input)
	{
		this.output = output;
		this.input = input;
		this.width = width;
		this.height = height;
	}
	
	public ItemStack getCraftingResult(IInventory inventory, int width, int heigt)
	{
		if(isValidRecipe(inventory, width, heigt))
			return output;
		return null;
	}
	
	public boolean isValidRecipe(IInventory inventory, int InvWidth, int InvHeigt)
	{
		if(InvWidth < this.width || InvHeigt < this.height)
			return false;
		
		int posX = 0;
		int posY = 0;
		caculateX:
		for (int x = 0; x < InvWidth; x++) {
			for (int y = 0; y < InvHeigt; y++) {
				if(inventory.getStackInSlot(x+y*InvHeigt) != null)
					break caculateX;
			}
			posX++;
		}
		
		caculateY:
		for (int y = 0; y < InvHeigt; y++) {
			for (int x = 0; x < InvWidth; x++) {
				if(inventory.getStackInSlot(x+y*InvHeigt) != null)
					break caculateY;
			}
			posY++;
		}
		
		//Convert stacks to the same format as the input
		ItemStack[] inv = new ItemStack[input.length];
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				inv[y*this.height+x] = inventory.getStackInSlot(x+y*InvHeigt);
			}
		}
		
		//Check if it's valid
		for (int i = 0; i < input.length; i++) {
			if(input[i] instanceof RecipeEntry)
			{
				if(!((RecipeEntry) input[i]).isEntry(inv[i]))
					return false;
			}else
				if(!RecipeEntry.isObject(inv[i], input[i]))
					return false;
		}
		return true;
	}
	
}
