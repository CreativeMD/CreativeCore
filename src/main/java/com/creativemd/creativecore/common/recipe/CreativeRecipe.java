package com.creativemd.creativecore.common.recipe;

import net.minecraft.item.ItemStack;

public class CreativeRecipe {
	
	public ItemStack output;
	public Object[] input;
	
	public CreativeRecipe(ItemStack output, Object... input)
	{
		this.output = output;
		this.input = input;
	}
	
}
