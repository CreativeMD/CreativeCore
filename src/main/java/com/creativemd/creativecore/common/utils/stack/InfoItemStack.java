package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class InfoItemStack extends InfoStack {
	
	public ItemStack stack;
	public boolean needNBT;
	
	public InfoItemStack(ItemStack stack, boolean needNBT, int stackSize)
	{
		super(stackSize);
		this.stack = stack;
		this.needNBT = needNBT;
	}
	
	public InfoItemStack(ItemStack stack, int stackSize)
	{
		this(stack, stack.hasTagCompound(), stackSize);
	}
	
	public InfoItemStack(ItemStack stack)
	{
		this(stack, stack.getCount());
	}
	
	public InfoItemStack() {
		super();
	}

	@Override
	protected void writeToNBTExtra(NBTTagCompound nbt) {
		stack.writeToNBT(nbt);
		nbt.setBoolean("needNBT", needNBT);
	}

	@Override
	protected void loadFromNBTExtra(NBTTagCompound nbt) {
		stack = new ItemStack(nbt);
		needNBT = nbt.getBoolean("needNBT");
	}

	@Override
	public boolean isInstanceIgnoreSize(InfoStack info) {
		if(info instanceof InfoItemStack)
		{
			if(((InfoItemStack) info).stack.getItem() != stack.getItem())
				return false;
			if(((InfoItemStack) info).stack.getItemDamage() != stack.getItemDamage())
				return false;
			if(((InfoItemStack) info).needNBT || this.needNBT)
			{
				if(((InfoItemStack) info).stack.getTagCompound() == null && stack.getTagCompound() == null)
					return true;
				if(((InfoItemStack) info).stack.getTagCompound() == null || stack.getTagCompound() == null)
					return false;
				return ((InfoItemStack) info).stack.getTagCompound().equals(stack.getTagCompound());
			}
			return true;
		}
		return false;
	}

	@Override
	public InfoStack copy() {
		return new InfoItemStack(stack.copy(), needNBT, stackSize);
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		ItemStack stack = new ItemStack(this.stack.getItem(), stacksize, this.stack.getItemDamage());
		if(needNBT && stack.hasTagCompound())
			stack.setTagCompound((NBTTagCompound) this.stack.getTagCompound().copy());
		return stack;
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		if(stack.getItem() != this.stack.getItem())
			return false;
		if(stack.getItemDamage() != this.stack.getItemDamage())
			return false;
		if(needNBT)
		{
			if(stack.getTagCompound() == this.stack.getTagCompound())
				return true;
			if(stack.getTagCompound() != null && this.stack.getTagCompound() != null)
				return stack.getTagCompound().equals(this.stack.getTagCompound());
			return false;
		}
		return true;
	}

	@Override
	public boolean equalsIgnoreSize(Object object) {
		return object instanceof InfoItemStack && ((InfoItemStack) object).isInstanceIgnoreSize(stack);
	}

	@Override
	public ArrayList<ItemStack> getAllPossibleItemStacks() {
		ArrayList<ItemStack> stacks = new ArrayList<>();
		stacks.add(getItemStack(1));
		return stacks;
	}

}
