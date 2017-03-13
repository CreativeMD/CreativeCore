package com.creativemd.creativecore.common.utils.stack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InfoMaterial extends InfoStack {
	
public Material material;
	
	public InfoMaterial(Material material, int stackSize)
	{
		super(stackSize);
		this.material = material;
	}
	
	public InfoMaterial(Material material)
	{
		this(material, 1);
	}
	
	public InfoMaterial() {
		super();
	}

	@Override
	protected void writeToNBTExtra(NBTTagCompound nbt) {
		String blockName = null;
		for (Object name : Block.REGISTRY.getKeys()) {
			Block block = Block.getBlockFromName((String) name);
			if(block != null && block.getDefaultState().getMaterial() == material)
			{
				blockName = (String) name;
				break;
			}
		}
		if(blockName != null)
			nbt.setString("material", blockName);
	}

	@Override
	protected void loadFromNBTExtra(NBTTagCompound nbt) {
		Block block = Block.getBlockFromName(nbt.getString("material"));
		if(block != null)
			material = block.getDefaultState().getMaterial();
	}

	@Override
	public boolean isInstanceIgnoreSize(InfoStack info) {
		if(info instanceof InfoMaterial)
			return ((InfoMaterial) info).material == material;
		if(info instanceof InfoBlock)
			return ((InfoBlock) info).block.getDefaultState().getMaterial() == material;
		if(info instanceof InfoItemStack)
		{
			Block block = Block.getBlockFromItem(((InfoItemStack) info).stack.getItem());
			if(block != null)
				return block.getStateFromMeta(((InfoItemStack) info).stack.getItemDamage()).getMaterial() == material;
		}
		return false;
	}

	@Override
	public InfoStack copy() {
		return new InfoMaterial(material, stackSize);
	}

	@Override
	public ItemStack getItemStack(int stacksize) {
		for (Object name : Block.REGISTRY.getKeys()) {
			Block block = Block.getBlockFromName((String) name);
			if(block != null && block.getDefaultState().getMaterial() == material)
			{
				return new ItemStack(block, stacksize);
			}
		}
		return null;
	}

	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if(block != null && !(block instanceof BlockAir))
			return block.getStateFromMeta(stack.getItemDamage()).getMaterial() == material;
		return false;
	}

	@Override
	public boolean equalsIgnoreSize(Object object) {
		return object instanceof InfoMaterial && ((InfoMaterial) object).material == this.material;
	}
	
}
