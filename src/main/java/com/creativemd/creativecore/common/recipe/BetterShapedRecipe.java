package com.creativemd.creativecore.common.recipe;

import com.creativemd.creativecore.common.utils.stack.InfoStack;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class BetterShapedRecipe implements IRecipe, IRecipeInfo {
	
	public InfoStack[] info;
	public ItemStack output;
	public int width;
	public int height;
	
	public BetterShapedRecipe(int width, InfoStack[] info, ItemStack output) {
		this.info = info;
		this.output = output;
		this.width = width;
		this.height = this.info.length / this.width;
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		for (int x = 0; x <= 3 - width; x++) {
			for (int y = 0; y <= 3 - height; ++y) {
				if (checkMatch(inv, x, y, false)) {
					return true;
				}
				
				if (checkMatch(inv, x, y, true)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int subX = x - startX;
				int subY = y - startY;
				InfoStack target = null;
				
				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = info[width - subX - 1 + subY * width];
					} else {
						target = info[subX + subY * width];
					}
				}
				
				ItemStack slot = inv.getStackInRowAndColumn(x, y);
				
				if (target == null && !slot.isEmpty())
					return false;
				if (target != null && slot.isEmpty())
					return false;
				if (target != null && !slot.isEmpty())
					if (!target.isInstanceIgnoreSize(slot))
						return false;
			}
		}
		
		return true;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		return output.copy();
	}
	
	@Override
	public int getRecipeSize() {
		return this.width * this.height;
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		return output.copy();
	}
	
	@Override
	public ItemStack[] getInput() {
		ItemStack[] stacks = new ItemStack[info.length];
		for (int i = 0; i < stacks.length; i++) {
			if (info[i] != null)
				stacks[i] = info[i].getItemStack();
		}
		return stacks;
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		
		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
		}
		
		return nonnulllist;
	}
	
}
