package com.creativemd.creativecore.common.utils.stack;

import com.creativemd.creativecore.common.utils.string.StringUtils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

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
		this(stack, stack.hasTagCompound(), stackSize);
	}
	
	public StackInfoItemStack(ItemStack stack)
	{
		this(stack, stack.stackSize);
	}

	@Override
	public StackInfo getStackInfo(Object item) {
		if(item instanceof ItemStack)
		{
			if(((ItemStack) item).getItemDamage() == OreDictionary.WILDCARD_VALUE)
			{
				if(((ItemStack) item).getItem() instanceof ItemBlock)
					return new StackInfoBlock(Block.getBlockFromItem(((ItemStack) item).getItem()));
				else
					return new StackInfoItem(((ItemStack) item).getItem());
			}
			return new StackInfoItemStack((ItemStack) item);
		}
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
				result.stack.setTagCompound((NBTTagCompound) objects[2]);
			}
			return result;
		}
		return null;
	}

	@Override
	public String toString() {
		if(needNBT)
		{
			if(!stack.hasTagCompound())
				return StringUtils.ObjectsToString(stack.getItem(), stack.getItemDamage(), true);
			else
				return StringUtils.ObjectsToString(stack.getItem(), stack.getItemDamage(), stack.getTagCompound());
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
			if(stack.getTagCompound() == this.stack.getTagCompound())
				return true;
			if(stack.getTagCompound() != null && this.stack.getTagCompound() != null)
				return stack.getTagCompound().equals(this.stack.getTagCompound());
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
				if(((StackInfoItemStack) info).stack.getTagCompound() == null && stack.getTagCompound() == null)
					return true;
				if(((StackInfoItemStack) info).stack.getTagCompound() == null || stack.getTagCompound() == null)
					return false;
				return ((StackInfoItemStack) info).stack.getTagCompound().equals(stack.getTagCompound());
			}
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		ItemStack stack = new ItemStack(this.stack.getItem(), stacksize, this.stack.getItemDamage());
		if(needNBT && stack.hasTagCompound())
			stack.setTagCompound((NBTTagCompound) this.stack.getTagCompound().copy());
		return stack;
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object)
	{
		return object instanceof StackInfoItemStack && ((StackInfoItemStack) object).isInstanceIgnoreSize(stack);
	}

	@Override
	public StackInfo copy() {
		return new StackInfoItemStack(stack.copy(), needNBT, stackSize);
	}

	@Override
	public String toTitle() {
		return stack.getDisplayName();
	}

}
