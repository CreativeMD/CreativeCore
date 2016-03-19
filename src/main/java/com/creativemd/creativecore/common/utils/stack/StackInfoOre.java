package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.List;

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
		if(item instanceof ArrayList)
		{
			ArrayList<Integer> oresIDs = new ArrayList<Integer>();
			ArrayList ores = (ArrayList) item;
			for (int i = 0; i < ores.size(); i++) {
				ArrayList<Integer> neworesIDs = new ArrayList<Integer>();
				int[] oreIDsofStack = OreDictionary.getOreIDs((ItemStack) ores.get(i));
				for (int j = 0; j < oreIDsofStack.length; j++)
					if(i == 0 || oresIDs.contains(oreIDsofStack[j]))
						neworesIDs.add(oreIDsofStack[j]);
				oresIDs = neworesIDs;
			}
			if(oresIDs.size() == 1)
				return new StackInfoOre(OreDictionary.getOreName(oresIDs.get(0)));
		}
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
		List<ItemStack> stacks = OreDictionary.getOres(ore);
		if(stacks.size() > 0)
		{
			ItemStack stack = stacks.get(0).copy();
			stack.stackSize = stacksize;
			return stack;
		}
		return null;
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object)
	{
		return object instanceof StackInfoOre && ((StackInfoOre) object).ore.equals(ore);
	}

	@Override
	public StackInfo copy() {
		return new StackInfoOre(ore.substring(0), stackSize);
	}

	@Override
	public String toTitle() {
		return "\"" + ore + "\"";
	}

}
