package com.creativemd.creativecore.common.utils.stack;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class StackInfoFuel extends StackInfo{
	
	public StackInfoFuel(int stackSize)
	{
		super(stackSize);
	}
	
	public StackInfoFuel()
	{
		this(0);
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
		return new ItemStack(Items.coal);
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
	public String toTitle() {
		return "Fuel";
	}
}
