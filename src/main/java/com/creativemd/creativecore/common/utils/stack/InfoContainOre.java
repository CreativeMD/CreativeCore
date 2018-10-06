package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class InfoContainOre extends InfoOre {
	
	public InfoContainOre(String name) {
		this(name, 1);
	}
	
	public InfoContainOre(String name, int stackSize) {
		super(name.toLowerCase(), stackSize);
	}
	
	public InfoContainOre() {
		super();
	}
	
	@Override
	public boolean isInstanceIgnoreSize(InfoStack info) {
		if (equalsIgnoreSize(info))
			return true;
		if (info instanceof InfoOre)
			return ((InfoOre) info).ore.toLowerCase().contains(ore);
		return false;
	}
	
	@Override
	public InfoStack copy() {
		return new InfoContainOre(ore, stackSize);
	}
	
	@Override
	public ItemStack getItemStack(int stacksize) {
		String[] names = OreDictionary.getOreNames();
		for (int i = 0; i < names.length; i++) {
			if (names[i].toLowerCase().contains(ore)) {
				List<ItemStack> stacks = OreDictionary.getOres(names[i]);
				if (stacks.size() > 0) {
					ItemStack stack = stacks.get(0).copy();
					if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
						stack.setItemDamage(0);
					stack.setCount(stacksize);
					return stack;
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		int[] ores = OreDictionary.getOreIDs(stack);
		for (int j = 0; j < ores.length; j++) {
			if (OreDictionary.getOreName(ores[j]).toLowerCase().contains(ore))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object) {
		return object instanceof InfoContainOre && ((InfoContainOre) object).ore.equals(ore);
	}
	
	@Override
	public ArrayList<ItemStack> getAllPossibleItemStacks() {
		ArrayList<ItemStack> result = new ArrayList<>();
		List<ItemStack> stacks = getAllExistingItems();
		for (int i = 0; i < stacks.size(); i++) {
			if (isInstanceIgnoreSize(stacks.get(i)))
				result.add(stacks.get(i));
		}
		return result;
	}
	
}
