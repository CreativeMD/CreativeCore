package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class StackInfoFuel extends StackInfo{
	
	public StackInfoFuel(int stackSize)
	{
		super(stackSize);
	}
	
	public InfoFuel() {
		super(1);
	}

	@Override
	public String toString() {
		return "f";
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		return TileEntityFurnace.isItemFuel(stack);
	}

	@Override
	public boolean isInstanceIgnoreSize(StackInfo info) {
		if(info instanceof StackInfoFuel)
			return true;
		return TileEntityFurnace.isItemFuel(info.getItemStack());
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		return new ItemStack(Items.COAL);
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object)
	{
		return object instanceof StackInfoFuel;
	}

	@Override
	public StackInfo copy() {
		return new StackInfoFuel(stackSize);
	}

	@Override
	public ArrayList<ItemStack> getAllPossibleItemStacks() {
		ArrayList<ItemStack> result = new ArrayList<>();
		List<ItemStack> stacks = getAllExistingItems();
		for (int i = 0; i < stacks.size(); i++) {
			if(isInstanceIgnoreSize(stacks.get(i)))
				result.add(stacks.get(i));
		}
		return result;
	}
}
