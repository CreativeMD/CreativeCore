package com.creativemd.creativecore.common.utils.stack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StackInfoBlock extends StackInfo implements IStackLoader{
	
	public Block block;
	
	public StackInfoBlock(Block block, int stackSize)
	{
		super(stackSize);
		this.block = block;
	}
	
	public StackInfoBlock(Block block)
	{
		this(block, 0);
	}

	@Override
	public StackInfo getStackInfo(Object item) {
		Block block = null;
		if(item instanceof Block)
			block = (Block) item;
		if(item instanceof Item)
			block = Block.getBlockFromItem((Item) item);
		if(block != null)
			return new StackInfoBlock(block);
		return null;
	}

	@Override
	public StackInfo getStackInfoFromString(String input) {
		Block block = (Block) Block.REGISTRY.getObject(new ResourceLocation(input));
		if(block != null && !(block instanceof BlockAir))
			return new StackInfoBlock(block);
		return null;
	}

	@Override
	public String toString() {
		return Block.REGISTRY.getNameForObject(block).toString();
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		return block == Block.getBlockFromItem(stack.getItem());
	}

	@Override
	public boolean isInstanceIgnoreSize(StackInfo info) {
		if(info instanceof StackInfoBlock)
			return ((StackInfoBlock) info).block == block;
		if(info instanceof StackInfoItemStack)
			return block == Block.getBlockFromItem(((StackInfoItemStack) info).stack.getItem());
		return false;
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object)
	{
		return object instanceof StackInfoBlock && ((StackInfoBlock) object).block == this.block;
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		return new ItemStack(block, stacksize);
	}

	@Override
	public StackInfo copy() {
		return new StackInfoBlock(block, stackSize);
	}

	@Override
	public String toTitle() {
		return block.getLocalizedName();
	}

}
