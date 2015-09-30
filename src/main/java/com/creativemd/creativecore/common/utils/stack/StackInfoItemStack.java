package com.creativemd.creativecore.common.utils.stack;

import com.creativemd.creativecore.common.utils.string.StringUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StackInfoItemStack extends StackInfo implements IStackLoader{
	
	public ItemStack stack;
	public boolean needNBT;
	
	public StackInfoItemStack(ItemStack stack, boolean needNBT, int stackSize)
	{
		super(stackSize);
		this.stack = stack;
		this.needNBT = needNBT;
	}
	
	public StackInfoItemStack(ItemStack stack, int stackSize)
	{
		this(stack, stack.stackTagCompound != null, stackSize);
	}
	
	public StackInfoItemStack(ItemStack stack)
	{
		this(stack, stack.stackSize);
	}

	@Override
	public StackInfo getStackInfo(Object item) {
		if(item instanceof ItemStack)
			return new StackInfoItemStack((ItemStack) item);
		return null;
	}

	@Override
	public StackInfo getStackInfoFromString(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		if(objects.length == 2 && objects[0] instanceof Item && objects[1] instanceof Integer)
		{
			return new StackInfoItemStack(new ItemStack((Item)objects[0], 1, (Integer)objects[1]));
		}else if(objects.length == 3 && objects[0] instanceof Item && objects[1] instanceof Integer){
			StackInfoItemStack result = new StackInfoItemStack(new ItemStack((Item)objects[0], 1, (Integer)objects[1]));
			result.needNBT = true;
			if(objects[2] instanceof NBTTagCompound){
				result.stack.stackTagCompound = (NBTTagCompound) objects[2];
			}
			return result;
		}
		return null;
	}

	@Override
	public String toString() {
		if(needNBT)
		{
			if(stack.stackTagCompound == null)
				return StringUtils.ObjectsToString(stack.getItem(), stack.getItemDamage(), true);
			else
				return StringUtils.ObjectsToString(stack.getItem(), stack.getItemDamage(), stack.stackTagCompound);
		}else{
			return StringUtils.ObjectsToString(stack.getItem(), stack.getItemDamage());
		}
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		if(stack.getItem() != this.stack.getItem())
			return false;
		if(stack.getItemDamage() != this.stack.getItemDamage())
			return false;
		if(needNBT)
		{
			if(stack.stackTagCompound == this.stack.stackTagCompound)
				return true;
			if(stack.stackTagCompound != null && this.stack.stackTagCompound != null)
				return stack.stackTagCompound.equals(this.stack.stackTagCompound);
			return false;
		}
		return true;
	}

	@Override
	public boolean isInstanceIgnoreSize(StackInfo info) {
		if(info instanceof StackInfoItemStack)
		{
			if(((StackInfoItemStack) info).stack.getItem() != stack.getItem())
				return false;
			if(((StackInfoItemStack) info).stack.getItemDamage() != stack.getItemDamage())
				return false;
			if(((StackInfoItemStack) info).needNBT || this.needNBT)
			{
				if(((StackInfoItemStack) info).stack.stackTagCompound == null && stack.stackTagCompound == null)
					return true;
				if(((StackInfoItemStack) info).stack.stackTagCompound == null || stack.stackTagCompound == null)
					return false;
				return ((StackInfoItemStack) info).stack.stackTagCompound.equals(stack.stackTagCompound);
			}
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		ItemStack stack = new ItemStack(this.stack.getItem(), stacksize, this.stack.getItemDamage());
		if(needNBT)
			stack.stackTagCompound = (NBTTagCompound) this.stack.stackTagCompound.copy();
		return stack;
	}
	
	@Override
	public boolean equals(Object object)
	{
		return object instanceof StackInfoItemStack && ((StackInfoItemStack) object).isInstance(stack) && ((StackInfoItemStack) object).stackSize == stackSize;
	}

	@Override
	public StackInfo copy() {
		return new StackInfoItemStack(stack.copy(), needNBT, stackSize);
	}

}
