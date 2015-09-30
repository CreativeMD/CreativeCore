package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public abstract class StackInfo{
	
	public static ArrayList<IStackLoader> loaders = new ArrayList<IStackLoader>();
	
	public static void registerLoader(IStackLoader loader)
	{
		loaders.add(loader);
	}
	
	public static StackInfo parseString(String input)
	{
		if(input == null)
			return null;
		
		try{
			for (int i = 0; i < loaders.size(); i++) {
				StackInfo temp = loaders.get(i).getStackInfoFromString(input);
				if(temp != null)
					return temp;
			}
		}catch(Exception e){
			return null;
		}
		return null;
	}
	
	public static StackInfo parseObject(Object stack)
	{
		if(stack == null)
			return null;
		
		for (int i = 0; i < loaders.size(); i++) {
			StackInfo temp = null;
			try{
				temp = loaders.get(i).getStackInfo(stack);
			}catch(Exception e){
				temp = null;
			}
			if(temp != null)
				return temp;
		}
		
		return null;
	}
	
	/**stacksize=0->stacksize ignored**/
	public int stackSize;
	
	public StackInfo()
	{
		this(0);
	}
	
	public StackInfo(int stackSize)
	{
		if(stackSize < 0)
			stackSize = 0;
		if(stackSize > 64)
			stackSize = 64;
		this.stackSize = stackSize;	
	}
	
	public static void registerDefaultLoaders()
	{
		registerLoader(new StackInfoItem(null));
		registerLoader(new StackInfoBlock(null));
		registerLoader(new StackInfoItemStack(null, false, 0));
		registerLoader(new StackInfoOre(""));
		registerLoader(new StackInfoMaterial(null));
	}
	
	public abstract String toString();
	
	protected abstract boolean isStackInstanceIgnoreSize(ItemStack stack);
	
	public boolean isInstanceIgnoreSize(ItemStack stack)
	{
		return isStackInstanceIgnoreSize(stack);
	}
	
	public int getAmount(ItemStack stack)
	{
		if(this.stackSize == 0)
			return Integer.MAX_VALUE;
		return stack.stackSize/stackSize;
	}
	
	public boolean isInstance(ItemStack stack)
	{
		if(isInstanceIgnoreSize(stack))
		{
			if(stackSize <= stack.stackSize)
				return true;
		}
		return false;
	}
	
	public boolean isInstance(StackInfo info)
	{
		if(isInstanceIgnoreSize(info))
		{
			if(stackSize < info.stackSize)
				return false;
		}
		return false;
	}
	
	public abstract boolean isInstanceIgnoreSize(StackInfo info);
	
	public abstract StackInfo copy();
	
	public ItemStack getItemStack()
	{
		return getItemStack(stackSize);
	}
	
	public abstract ItemStack getItemStack(int stacksize);
	
}
