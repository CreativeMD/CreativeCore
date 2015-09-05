package com.creativemd.creativecore.common.recipe;

import net.minecraft.item.ItemStack;

public interface IRecipeInfo {
	
	public ItemStack[] getInput();
	
	public int getWidth();

}
