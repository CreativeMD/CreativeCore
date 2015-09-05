package com.creativemd.creativecore.common.utils.stack;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class StackInfoItem extends StackInfo implements IStackLoader{
	
	public Item item;
	
	public StackInfoItem(Item item)
	{
		this(item, 0);
	}
	
	public StackInfoItem(Item item, int stackSize)
	{
		super(stackSize);
		this.item = item;
	}

	@Override
	public StackInfo getStackInfo(Object item) {
		if(item instanceof Item && !(item instanceof ItemBlock))
			return new StackInfoItem((Item) item);
		return null;
	}

	@Override
	public StackInfo getStackInfoFromString(String input) {
		Item item = (Item) Item.itemRegistry.getObject(input);
		if(item != null)
			return new StackInfoItem(item);
		return null;
	}

	@Override
	public String toString() {
		return Item.itemRegistry.getNameForObject(item);
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		return stack.getItem() == item;
	}

	@Override
	public boolean isInstanceIgnoreSize(StackInfo info) {
		if(info instanceof StackInfoItem)
			return ((StackInfoItem) info).item == item;
		if(info instanceof StackInfoItemStack)
			return item == ((StackInfoItemStack) info).stack.getItem();
		return false;
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		return new ItemStack(item, stacksize);
	}
	

}
