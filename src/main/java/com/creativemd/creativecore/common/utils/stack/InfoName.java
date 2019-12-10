package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class InfoName extends InfoStack {
	
	public String name;
	
	public InfoName(String name, int stackSize) {
		super(stackSize);
		this.name = name;
	}
	
	public InfoName(String name) {
		this(name, 1);
	}
	
	public InfoName() {
		super();
	}
	
	@Override
	protected void writeToNBTExtra(NBTTagCompound nbt) {
		nbt.setString("name", name);
	}
	
	@Override
	protected void loadFromNBTExtra(NBTTagCompound nbt) {
		name = nbt.getString("name");
	}
	
	@Override
	public boolean isInstanceIgnoreSize(InfoStack info) {
		if (info instanceof InfoName)
			return ((InfoName) info).name.contains(name);
		return false;
	}
	
	@Override
	public InfoStack copy() {
		return new InfoName(name, stackSize);
	}
	
	@Override
	public ItemStack getItemStack(int stacksize) {
		for (Iterator<ResourceLocation> iterator = Block.REGISTRY.getKeys().iterator(); iterator.hasNext();) {
			ResourceLocation location = iterator.next();
			if (location.toString().contains(name))
				return new ItemStack(Block.REGISTRY.getObject(location), stackSize);
		}
		
		for (Iterator<ResourceLocation> iterator = Item.REGISTRY.getKeys().iterator(); iterator.hasNext();) {
			ResourceLocation location = iterator.next();
			if (location.toString().contains(name))
				return new ItemStack(Item.REGISTRY.getObject(location), stackSize);
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
		if (stack.getItem() instanceof ItemBlock)
			return Block.REGISTRY.getNameForObject(Block.getBlockFromItem(stack.getItem())).toString().contains(name);
		ResourceLocation item = Item.REGISTRY.getNameForObject(stack.getItem());
		return item != null ? item.toString().contains(name) : false;
	}
	
	@Override
	public boolean equalsIgnoreSize(Object object) {
		return object instanceof InfoName && ((InfoName) object).name.equals(name);
	}
	
	@Override
	public ArrayList<ItemStack> getAllPossibleItemStacks() {
		ArrayList<ItemStack> result = new ArrayList<>();
		List<ItemStack> stacks = getAllExistingItems();
		for (int i = 0; i < stacks.size(); i++) {
			if (isInstanceIgnoreSize(stacks.get(i)))
				result.add(stacks.get(i));
		}
		return result;
	}
}
