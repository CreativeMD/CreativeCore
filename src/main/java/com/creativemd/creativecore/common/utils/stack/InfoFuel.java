package com.creativemd.creativecore.common.utils.stack;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;

public class InfoFuel extends InfoStack {
	
	public InfoFuel(int stackSize) {
		super(stackSize);
	}
	
	public InfoFuel() {
		super();
	}

	@Override
	protected void writeToNBTExtra(NBTTagCompound nbt) {
		
	}

	@Override
	protected void loadFromNBTExtra(NBTTagCompound nbt) {
		
	}

	@Override
	public boolean isInstanceIgnoreSize(InfoStack info) {
		return TileEntityFurnace.isItemFuel(info.getItemStack());
	}

	@Override
	public InfoStack copy() {
		return new InfoFuel(stackSize);
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		return new ItemStack(Items.COAL);
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		return TileEntityFurnace.isItemFuel(stack);
	}

	@Override
	public boolean equalsIgnoreSize(Object object) {
		return object instanceof InfoFuel;
	}

}
