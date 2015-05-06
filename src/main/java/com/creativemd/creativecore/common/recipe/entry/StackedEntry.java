package com.creativemd.creativecore.common.recipe.entry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StackedEntry extends RecipeEntry{
	
	public Object item;
	/**Maybe a block/item/ore need a custom nbt, because only itemstacks have a nbttag*/
	public NBTTagCompound nbt = null;
	/**If minStackSize is 0, the item won't be used, but it is still needs to be in place**/
	public int minStackSize = 1;
	
	public StackedEntry(Object item, int stackSize)
	{
		this.minStackSize = stackSize;
	}
	
	public StackedEntry(Object item, int stackSize, NBTTagCompound nbt)
	{
		this(item, stackSize);
		this.nbt = nbt;
	}
	
	@Override
	public boolean isEntry(ItemStack stack) {
		if(!isObject(stack, item))
		{
			if(stack.stackSize >= minStackSize)
			{
				if(nbt != null)
				{
					if(stack.stackTagCompound != null)
						return stack.stackTagCompound.equals(nbt);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int getStackSize(ItemStack stack) {
		if(minStackSize == 0)
			return Integer.MAX_VALUE;
		return stack.stackSize/minStackSize;
	}

}
