package com.creativemd.creativecore.common.recipe;

import com.creativemd.creativecore.common.recipe.entry.RecipeEntry;
import com.creativemd.creativecore.common.utils.stack.InfoStack;
import com.ibm.icu.impl.UBiDiProps;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class GridRecipe extends Recipe{
	public int width;
	public int height;
	
	public GridRecipe(ItemStack[] output, int width, int height, Object... input)
	{
		super(output, input);
		this.width = width;
		this.height = height;
	}
	
	public GridRecipe(ItemStack[] output, int width, int height, InfoStack... input)
	{
		super(output, input);
		this.width = width;
		this.height = height;
	}
	
	public void consumeRecipe(IInventory inventory, int width, int heigt)
	{
		consumeRecipe(inventory, width, heigt, 1);
	}
	
	public void consumeRecipe(IInventory inventory, int width, int heigt, int amount)
	{
		ItemStack[] inv = getObjectInValidOrder(inventory, width, heigt);
		
		for (int i = 0; i < inv.length; i++) {
			if(inv[i] != null)
			{
				ItemStack stack = inv[i];
				
				int index = indexOf(inventory, stack);
				if(index != -1)
				{
					if(input[i].isInstance(stack))
					{
						stack.shrink(input[i].stackSize*amount);
						if(stack.isEmpty())
							inventory.setInventorySlotContents(index, ItemStack.EMPTY);
					}
				}
				
			}
		}
	}
	
	public int getNumberofResults(IInventory inventory, int InvWidth, int InvHeigt)
	{
		ItemStack[] inv = getObjectInValidOrder(inventory, InvWidth, InvHeigt);
		
		if(isValidRecipe(inv))
		{
			int number = Integer.MAX_VALUE;
			for (int i = 0; i < input.length; i++) {
				int uses = inv[i].getCount()/input[i].stackSize;
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
		if(gridWidth != this.width || gridHeight != this.height)
			return null;
		
		//Convert stacks to the same format as the input
		ItemStack[] inv = new ItemStack[input.length];
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				inv[y*this.width+x] = inventory.getStackInSlot(x+y*InvWidth);
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
	
	protected boolean isStackValid(ItemStack stack, InfoStack input)
	{
		if(stack == null && input == null)
			return true;
		if(input != null)
			return input.isInstance(stack);
		return false;
	}
	
	protected boolean isValidRecipe(ItemStack[] inv)
	{
		//Check if it's valid
		for (int i = 0; i < input.length; i++) {
			if(!isStackValid(inv[i], input[i]))
				return false;
		}
		return true;
	}
}
