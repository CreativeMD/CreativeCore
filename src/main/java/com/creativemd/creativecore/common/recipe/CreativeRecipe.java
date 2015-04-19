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
	
	public int getNumberofResults(IInventory inventory, int InvWidth, int InvHeigt)
	{
		ItemStack[] inv = getObjectInValidOrder(inventory, InvWidth, InvHeigt);
		
		if(isValidRecipe(inv))
		{
			int number = Integer.MAX_VALUE;
			for (int i = 0; i < input.length; i++) {
				int uses = 0;
				if(input[i] instanceof RecipeEntry)
				{
					uses = ((RecipeEntry) input[i]).getNumberofUses(inv[i]);
					
				}else if(input[i] instanceof ItemStack){
					uses = inv[i].stackSize/((ItemStack)input[i]).stackSize;			
				}else{
					uses = inv[i].stackSize;
				}
				number = Math.min(uses, number);
			}
			return number;
		}
		return 0;
	}
	
	protected ItemStack[] getObjectInValidOrder(IInventory inventory, int InvWidth, int InvHeigt)
	{
		if(InvWidth < this.width || InvHeigt < this.height)
			return null;
				
		int posX = 0;
		int posY = 0;
		int posX2 = -1;
		int posY2 = -1;
		boolean foundX = false;
		for (int x = 0; x < InvWidth; x++) {
			for (int y = 0; y < InvHeigt; y++) {
				if(inventory.getStackInSlot(x+y*InvHeigt) != null)
				{
					foundX = true;
					posX2 = x;
				}
			}
			if(!foundX)
				posX++;
		}
		
		boolean foundY = false;
		for (int y = 0; y < InvHeigt; y++) {
			for (int x = 0; x < InvWidth; x++) {
				if(inventory.getStackInSlot(x+y*InvHeigt) != null)
				{
					foundY = true;
					posY2 = y;
				}
			}
			if(!foundY)
				posY++;
		}
		
		if(posX2 < 0 ||posY2 < 0)
		{
			return null;
		}
		
		int gridWidth = posX2 - posX + 1;
		int gridHeight = posY2 - posY + 1;
		if(gridWidth != this.width || gridWidth != this.height)
			return null;
		
		//Convert stacks to the same format as the input
		ItemStack[] inv = new ItemStack[input.length];
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				inv[y*this.height+x] = inventory.getStackInSlot(x+y*InvHeigt);
			}
		}
		
		return inv;
		
	}
	
	public boolean isValidRecipe(IInventory inventory, int InvWidth, int InvHeigt)
	{
		ItemStack[] inv = getObjectInValidOrder(inventory, InvWidth, InvHeigt);
		
		if(inv == null)
			return false;
		return isValidRecipe(inv);
	}
	
	public boolean isStackValid(ItemStack stack, Object input)
	{
		if(input instanceof RecipeEntry)
		{
			if(!((RecipeEntry) input).isEntry(stack))
				return false;
		}else{
			if(!RecipeEntry.isObject(stack, input))
				return false;
		}
		return true;
	}
	
	public boolean isValidRecipe(ItemStack[] inv)
	{
		//Check if it's valid
		for (int i = 0; i < input.length; i++) {
			if(!isStackValid(inv[i], input[i]))
				return false;
		}
		return true;
	}
	
}
