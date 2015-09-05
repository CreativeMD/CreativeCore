package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class StackInfoOre extends StackInfo implements IStackLoader{
	
	public String ore;
	
	public StackInfoOre(String ore, int stackSize)
	{
		super(stackSize);
		this.ore = ore;
	}
	
	public StackInfoOre(String ore)
	{
		this(ore, 0);
	}

	@Override
	public StackInfo getStackInfo(Object item) {
		if(item instanceof String)
			return new StackInfoOre((String) item);
		return null;
	}

	@Override
	public StackInfo getStackInfoFromString(String input) {
		return new StackInfoOre(input);
	}

	@Override
	public String toString() {
		return ore;
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		int[] ores = OreDictionary.getOreIDs(stack);
		for (int i = 0; i < ores.length; i++)
			if(OreDictionary.getOreName(ores[i]).equals(ore))
				return true;
		return false;
	}

	@Override
	public boolean isInstanceIgnoreSize(StackInfo info) {
		if(info instanceof StackInfoOre)
			return ((StackInfoOre) info).ore.equals(ore);
		return false;
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		ArrayList<ItemStack> stacks = OreDictionary.getOres(ore);
		if(stacks.size() > 0)
		{
			ItemStack stack = stacks.get(0).copy();
			stack.stackSize = stacksize;
			return stack;
		}
		return null;
	}

}
