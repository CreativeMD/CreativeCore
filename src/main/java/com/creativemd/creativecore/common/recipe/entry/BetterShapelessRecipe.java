package com.creativemd.creativecore.common.recipe.entry;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

import com.creativemd.creativecore.common.recipe.IRecipeInfo;
import com.creativemd.creativecore.common.utils.stack.StackInfo;

public class BetterShapelessRecipe implements IRecipe, IRecipeInfo{

	public ArrayList<StackInfo> info;
	public ItemStack output;
	public int width;
	
	public BetterShapelessRecipe(ArrayList<StackInfo> info, ItemStack output)
	{
		this.info = info;
		this.output = output;
		this.width = Math.min(info.size(), 3);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world)
    {
        ArrayList<StackInfo> list = new ArrayList(this.info);
        
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (itemstack != null)
                {
                    boolean flag = false;
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        StackInfo stackInfo = (StackInfo)iterator.next();

                        if (stackInfo.isInstance(itemstack))
                        {
                            flag = true;
                            list.remove(stackInfo);
                            break;
                        }
                    }

                    if (!flag)
                    {
                        return false;
                    }
                }
            }
        }

        return list.isEmpty();
    }
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		return output.copy();
	}

	@Override
	public int getRecipeSize() {
		return info.size();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output.copy();
	}

	@Override
	public ItemStack[] getInput() {
		ItemStack[] stacks = new ItemStack[info.size()];
		for (int i = 0; i < stacks.length; i++) {
			if(info.get(i) != null)
				stacks[i] = info.get(i).getItemStack();
		}
		return stacks;
	}

	@Override
	public int getWidth() {
		return this.width;
	}
}
