package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class InfoOre extends InfoStack {

	public String ore;

	public InfoOre(String ore, int stackSize) {
		super(stackSize);
		this.ore = ore;
	}

	public InfoOre(String ore) {
		this(ore, 1);
	}

	public InfoOre() {
		super();
	}

	@Override
	protected void writeToNBTExtra(NBTTagCompound nbt) {
		nbt.setString("ore", ore);
	}

	@Override
	protected void loadFromNBTExtra(NBTTagCompound nbt) {
		ore = nbt.getString("ore");
	}

	@Override
	public boolean isInstanceIgnoreSize(InfoStack info) {
		if (info instanceof InfoOre)
			return ((InfoOre) info).ore.equals(ore);
		return false;
	}

	@Override
	public InfoStack copy() {
		return new InfoOre(ore, stackSize);
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		List<ItemStack> stacks = OreDictionary.getOres(ore);
		if (stacks.size() > 0) {
			ItemStack stack = stacks.get(0).copy();
			if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
				stack.setItemDamage(0);
			stack.setCount(stacksize);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		int[] ores = OreDictionary.getOreIDs(stack);
		for (int i = 0; i < ores.length; i++)
			if (OreDictionary.getOreName(ores[i]).equals(ore))
				return true;
		return false;
	}

	@Override
	public boolean equalsIgnoreSize(Object object) {
		return object instanceof InfoOre && ((InfoOre) object).ore.equals(ore);
	}

	@Override
	public ArrayList<ItemStack> getAllPossibleItemStacks() {
		return new ArrayList<>(OreDictionary.getOres(ore));
	}
}
