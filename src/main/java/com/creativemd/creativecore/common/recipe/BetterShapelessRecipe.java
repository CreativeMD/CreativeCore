package com.creativemd.creativecore.common.recipe;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import com.creativemd.creativecore.common.utils.stack.InfoStack;

public class BetterShapelessRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe, IRecipeInfo{

	public ArrayList<InfoStack> info;
	public ItemStack output;
	public int width;
	
	public BetterShapelessRecipe(ArrayList<InfoStack> info, ItemStack output)
	{
		this.info = info;
		this.output = output;
		this.width = Math.min(info.size(), 3);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world)
    {
        ArrayList<InfoStack> list = new ArrayList(this.info);
        
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (!itemstack.isEmpty())
                {
                    boolean flag = false;
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        InfoStack stackInfo = (InfoStack)iterator.next();

                        if (stackInfo.isInstanceIgnoreSize(itemstack))
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
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
		NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return nonnulllist;
    }

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= this.info.size();
	}
}
