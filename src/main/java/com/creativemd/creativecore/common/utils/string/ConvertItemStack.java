package com.creativemd.creativecore.common.utils.string;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

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
		if(Block.getBlockFromItem(stack.getItem()) != null)
		{
			ResourceLocation resource = Block.REGISTRY.getNameForObject((Block) Block.getBlockFromItem(stack.getItem()));
			if(resource != null)
				item = resource.toString();
		}else{
			ResourceLocation resource = Item.REGISTRY.getNameForObject((Item) stack.getItem());
			if(resource != null)
				item = resource.toString();
		}
		if(!stack.hasTagCompound())
			return StringUtils.ObjectsToString(item, stack.stackSize, stack.getItemDamage(), "null");
		else
			return StringUtils.ObjectsToString(item, stack.stackSize, stack.getItemDamage(), stack.getTagCompound());
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		if(objects.length == 4 && objects[0] instanceof String && objects[1] instanceof Integer && objects[2] instanceof Integer && (objects[3] instanceof NBTTagCompound || objects[3] instanceof String))
		{
			ItemStack stack = null;
			ResourceLocation location = new ResourceLocation((String)objects[0]);
			if(Item.REGISTRY.getObject(location) != null)
				stack = new ItemStack((Item)Item.REGISTRY.getObject(location));
			else if(Block.REGISTRY.getObject(location) != null)
				stack = new ItemStack((Block)Block.REGISTRY.getObject(location));
			else
				return null;
			stack.stackSize = (Integer)objects[1];
			stack.setItemDamage((Integer)objects[2]);
			if(objects[3] instanceof NBTTagCompound)
				stack.setTagCompound((NBTTagCompound)objects[3]);
			return stack;
		}
		return null;
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}
	
	

}
