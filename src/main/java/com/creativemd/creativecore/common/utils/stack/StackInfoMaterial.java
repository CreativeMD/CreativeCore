package com.creativemd.creativecore.common.utils.stack;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class StackInfoMaterial extends StackInfo implements IStackLoader{
	
	public Material material;
	
	public StackInfoMaterial(Material material, int stackSize)
	{
		super(stackSize);
		this.material = material;
	}
	
	public StackInfoMaterial(Material material)
	{
		this(material, 0);
	}

	@Override
	public StackInfo getStackInfo(Object item) {
		if(item instanceof Material)
		{
			return new StackInfoMaterial((Material) item);
		}
		return null;
	}

	@Override
	public StackInfo getStackInfoFromString(String input) {
		Block block = Block.getBlockFromName(input);
		if(block != null)
			return new StackInfoMaterial(block.getMaterial(null));
		return null;
	}

	@Override
	public String toString() {
		for (Object name : Block.blockRegistry.getKeys()) {
			Block block = Block.getBlockFromName((String) name);
			if(block != null && block.getMaterial(null) == material)
				return (String) name;				
		}
		return null;
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if(block != null)
		{
			return block.getMaterial(null) == material;
		}
		return false;
	}

	@Override
	public boolean isInstanceIgnoreSize(StackInfo info) {
		if(info instanceof StackInfoMaterial)
			return ((StackInfoMaterial) info).material == material;
		if(info instanceof StackInfoBlock)
			return ((StackInfoBlock) info).block.getMaterial(null) == material;
		if(info instanceof StackInfoItemStack)
		{
			Block block = Block.getBlockFromItem(((StackInfoItemStack) info).stack.getItem());
			if(block != null)
				return block.getMaterial(null) == material;
		}
		return false;
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		for (Object name : Block.blockRegistry.getKeys()) {
			Block block = Block.getBlockFromName((String) name);
			if(block != null && block.getMaterial(null) == material)
			{
				return new ItemStack(block, stacksize);
			}
		}
		return null;
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object)
	{
		return object instanceof StackInfoMaterial && ((StackInfoMaterial) object).material == this.material;
	}

	@Override
	public StackInfo copy() {
		return new StackInfoMaterial(material, stackSize);
	}

	@Override
	public String toTitle() {
		return material.toString();
	}

}
