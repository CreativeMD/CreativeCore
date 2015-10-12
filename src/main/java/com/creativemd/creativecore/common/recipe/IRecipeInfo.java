package com.creativemd.creativecore.common.recipe;

import java.lang.reflect.Method;
import java.util.List;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public interface IRecipeInfo {
	
	public Object[] getInput();
	
	public int getWidth();

}
