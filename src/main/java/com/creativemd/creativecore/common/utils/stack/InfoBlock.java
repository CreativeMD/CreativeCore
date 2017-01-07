package com.creativemd.creativecore.common.utils.stack;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class InfoBlock extends InfoStack {
	
	public Block block;
	
	public InfoBlock(Block block, int stackSize)
	{
		super(stackSize);
		this.block = block;
	}
	
	public InfoBlock(Block block)
	{
		this(block, 0);
	}
	
	public InfoBlock() {
		super();
	}

	@Override
	protected void writeToNBTExtra(NBTTagCompound nbt) {
		nbt.setString("block", Block.REGISTRY.getNameForObject(block).toString());
	}

	@Override
	protected void loadFromNBTExtra(NBTTagCompound nbt) {
		block = (Block) Block.REGISTRY.getObject(new ResourceLocation(nbt.getString("block")));
	}

	@Override
	public boolean isInstanceIgnoreSize(InfoStack info) {
		if(info instanceof InfoBlock)
			return ((InfoBlock) info).block == block;
		if(info instanceof InfoItemStack)
			return block == Block.getBlockFromItem(((InfoItemStack) info).stack.getItem());
		return false;
	}

	@Override
	public InfoStack copy() {
		return new InfoBlock(block, stackSize);
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		return new ItemStack(block, stacksize);
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		return block == Block.getBlockFromItem(stack.getItem());
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object)
	{
		return object instanceof InfoBlock && ((InfoBlock) object).block == this.block;
	}

}
