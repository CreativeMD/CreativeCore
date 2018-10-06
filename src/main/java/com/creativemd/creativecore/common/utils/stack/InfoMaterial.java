package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class InfoMaterial extends InfoStack {
	
	public Material material;
	
	public InfoMaterial(Material material, int stackSize) {
		super(stackSize);
		this.material = material;
	}
	
	public InfoMaterial(Material material) {
		this(material, 1);
	}
	
	public InfoMaterial() {
		super();
	}
	
	@Override
	protected void writeToNBTExtra(NBTTagCompound nbt) {
		ResourceLocation blockName = null;
		for (Block block : Block.REGISTRY) {
			if (block != null && block.getDefaultState().getMaterial() == material) {
				blockName = block.getRegistryName();
				break;
			}
		}
		if (blockName != null)
			nbt.setString("material", blockName.toString());
	}
	
	@Override
	protected void loadFromNBTExtra(NBTTagCompound nbt) {
		Block block = Block.getBlockFromName(nbt.getString("material"));
		if (block != null)
			material = block.getDefaultState().getMaterial();
	}
	
	@Override
	public boolean isInstanceIgnoreSize(InfoStack info) {
		if (info instanceof InfoMaterial)
			return ((InfoMaterial) info).material == material;
		if (info instanceof InfoBlock)
			return ((InfoBlock) info).block.getDefaultState().getMaterial() == material;
		if (info instanceof InfoItemStack) {
			Block block = Block.getBlockFromItem(((InfoItemStack) info).stack.getItem());
			if (block != null)
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
		
		for (Iterator<Block> iterator = Block.REGISTRY.iterator(); iterator.hasNext();) {
			Block block = iterator.next();
			if (block != null && block.getDefaultState().getMaterial() == material) {
				NonNullList<ItemStack> stacks = NonNullList.create();
				
				try {
					block.getSubBlocks(Item.getItemFromBlock(block), (CreativeTabs) displayOnCreativeTab.get(block), stacks);
					if (!stacks.isEmpty() && !stacks.get(0).isEmpty())
						return stacks.get(0).copy();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block != null && !(block instanceof BlockAir))
			return block.getStateFromMeta(stack.getItemDamage()).getMaterial() == material;
		return false;
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object) {
		return object instanceof InfoMaterial && ((InfoMaterial) object).material == this.material;
	}
	
	@Override
	public ArrayList<ItemStack> getAllPossibleItemStacks() {
		ArrayList<ItemStack> result = new ArrayList<>();
		NonNullList<ItemStack> stacks = NonNullList.create();
		
		Iterator iterator = Block.REGISTRY.iterator();
		
		while (iterator.hasNext()) {
			Block block = (Block) iterator.next();
			
			try {
				block.getSubBlocks(Item.getItemFromBlock(block), (CreativeTabs) displayOnCreativeTab.get(block), stacks);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < stacks.size(); i++) {
			if (isInstanceIgnoreSize(stacks.get(i)))
				result.add(stacks.get(i));
		}
		return result;
	}
}
