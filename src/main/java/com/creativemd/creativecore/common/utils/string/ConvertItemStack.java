package com.creativemd.creativecore.common.utils.string;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ConvertItemStack extends StringConverter{

	public ConvertItemStack() {
		super("ItemStack");
	}

	@Override
	public Class getClassOfObject() {
		return ItemStack.class;
	}

	@Override
	public String toString(Object object) {
		ItemStack stack = (ItemStack) object;
		if(stack == null || stack.getItem() == null)
			return "null";
		String item = "";
		if(Block.getBlockFromItem(stack.getItem()) instanceof BlockAir)
			item = Item.itemRegistry.getNameForObject(stack.getItem());
		else
			item = Block.blockRegistry.getNameForObject(Block.getBlockFromItem(stack.getItem()));
		if(stack.stackTagCompound == null)
			return StringUtils.ObjectsToString(item, stack.stackSize, stack.getItemDamage(), "null");
		else
			return StringUtils.ObjectsToString(item, stack.stackSize, stack.getItemDamage(), stack.stackTagCompound);
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		if(objects.length == 4 && objects[0] instanceof String && objects[1] instanceof Integer && objects[2] instanceof Integer && (objects[3] instanceof NBTTagCompound || objects[3] instanceof String))
		{
			ItemStack stack = null;
			if(Item.itemRegistry.getObject((String)objects[0]) != null)
				stack = new ItemStack((Item)Item.itemRegistry.getObject((String)objects[0]));
			else if(!(Block.blockRegistry.getObject((String)objects[0]) instanceof BlockAir))
				stack = new ItemStack((Block)Block.blockRegistry.getObject((String)objects[0]));
			else
				return null;
			stack.stackSize = (Integer)objects[1];
			stack.setItemDamage((Integer)objects[2]);
			if(objects[3] instanceof NBTTagCompound)
				stack.stackTagCompound = (NBTTagCompound)objects[3];
			return stack;
		}
		return null;
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}
	
	

}
