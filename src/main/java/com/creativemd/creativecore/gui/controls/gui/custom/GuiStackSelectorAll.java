package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.creativemd.creativecore.common.utils.HashMapList;
import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class GuiStackSelectorAll extends GuiStackSelector {
	
	public StackCollector collector;

	public GuiStackSelectorAll(String name, int x, int y, int width, EntityPlayer player, StackCollector collector) {
		super(name, x, y, width, player);
		this.collector = collector;
		updateCollectedStacks();
		selectFirst();
	}
	
	@Override
	protected HashMapList<String, ItemStack> collectItems() {
		if(collector != null)
			return this.collector.collect(getPlayer());
		return null;
	}
	
	public void setSelectedForce(ItemStack stack)
	{
		if(!setSelected(stack))
		{
			String display;
			try{
				display = stack.getDisplayName();
			}catch(Exception e){
				display = Item.REGISTRY.getNameForObject(stack.getItem()).toString();
			}
			caption = display;
			
			this.selected = stack;
			raiseEvent(new GuiControlChangedEvent(this));
		}
	}
	
	public static StackCollector getCollectorFromPlayer(EntityPlayer player, StackSelector selector)
	{
		if(player.isCreative())
			return new CreativeCollector(selector);
		return new InventoryCollector(selector);
	}
	
	public static abstract class StackCollector {
		
		public StackSelector selector;
		
		public StackCollector(StackSelector selector) {
			this.selector = selector;
		}
		
		public abstract HashMapList<String, ItemStack> collect(EntityPlayer player);
		
	}
	
	public static class InventoryCollector extends StackCollector {

		public InventoryCollector(StackSelector selector) {
			super(selector);
		}

		@Override
		public HashMapList<String, ItemStack> collect(EntityPlayer player) {
			HashMapList<String, ItemStack> stacks = new HashMapList<>();
			
			//Inventory
			List<ItemStack> tempStacks = new ArrayList<>();
			for(ItemStack stack : player.inventory.mainInventory)
				if(!stack.isEmpty() && selector.allow(stack))
					tempStacks.add(stack.copy());
			stacks.add("selector.inventory", tempStacks);
			
			return stacks;
		}
		
	}
	
	public static class CreativeCollector extends InventoryCollector {

		public CreativeCollector(StackSelector selector) {
			super(selector);
		}
		
		@Override
		public HashMapList<String, ItemStack> collect(EntityPlayer player) {
			HashMapList<String, ItemStack> stacks = super.collect(player);
			
			NonNullList<ItemStack> tempStacks = NonNullList.create();
			Iterator iterator = Item.REGISTRY.iterator();
			while (iterator.hasNext())
	        {
	            Item item = (Item)iterator.next();
	
	            if (item != null && item.getCreativeTab() != null)
	            {
	                item.getSubItems(CreativeTabs.SEARCH, tempStacks);
	            }
	        }
			
			iterator = Block.REGISTRY.iterator();
			while (iterator.hasNext())
	        {
				Block block = (Block)iterator.next();
	
	            if (block != null && block.getCreativeTabToDisplayOn() != null)
	            {
	            	block.getSubBlocks(CreativeTabs.SEARCH, tempStacks);
	            }
	        }
			
			List<ItemStack> newStacks = new ArrayList<>();
			for (ItemStack stack : tempStacks) {
				if(!stack.isEmpty() && selector.allow(stack))
					newStacks.add(stack);
			}
			stacks.add("selector.all", newStacks);
			
			return stacks;
		}
	}
	
	public static abstract class StackSelector {
		
		public abstract boolean allow(ItemStack stack);
		
	}
	
	public static class SearchSelector extends StackSelector {
		
		
		public static String getItemName(ItemStack stack)
		{
			String itemName = "";
			try
			{
				itemName = stack.getDisplayName();
			}catch(Exception e){
				if(Block.getBlockFromItem(stack.getItem()) != null)
					itemName = Block.REGISTRY.getNameForObject(Block.getBlockFromItem(stack.getItem())).toString();
				else
					itemName = Item.REGISTRY.getNameForObject(stack.getItem()).toString();
			}
			return itemName;
		}
		
		public String search = "";
		
		public boolean allow(ItemStack stack)
		{
			if(search.equals(""))
				return true;
			return getItemName(stack).toLowerCase().contains(search);
		}
		
	}
	
	public static class BlockSelector extends SearchSelector {
		
		@Override
		public boolean allow(ItemStack stack) {
			if(super.allow(stack))
				return Block.getBlockFromItem(stack.getItem()) != null && !(Block.getBlockFromItem(stack.getItem()) instanceof BlockAir);
			return false;
		}
		
	}

}
