package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;
import java.util.Iterator;

import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.controls.container.SlotControlNoSync;
import com.creativemd.creativecore.gui.controls.container.client.GuiSlotControl;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBoxExtension;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.gui.controls.gui.custom.GuiInvSelector.StackSelector;
import com.creativemd.creativecore.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.slots.SlotPreview;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class GuiItemStackSelector extends GuiComboBoxExtension{
	
	//public GuiInvSelectorExtension extension;
	public NonNullList<ItemStack> stacks;
	public NonNullList<ItemStack> inv;
	
	public StackSelector selector;
	
	public GuiItemStackSelector(String name, EntityPlayer player, int x, int y, int width, int height, GuiComboBox comboBox, StackSelector selector) {
		super(name, comboBox, x, y, width, height, new ArrayList<String>());
		this.selector = selector;
		//this.extension = extension;
		
		stacks = NonNullList.create();
		inv = NonNullList.create();
		
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			if(!player.inventory.mainInventory.get(i).isEmpty())
				inv.add(player.inventory.mainInventory.get(i).copy());
		}
		//CreativeTabs.tabAllSearch.displayAllReleventItems(stacks);
		Iterator iterator = Item.REGISTRY.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item != null && item.getCreativeTab() != null)
            {
                item.getSubItems(item, (CreativeTabs)null, stacks);
            }
        }
        
        iterator = Block.REGISTRY.iterator();
		while (iterator.hasNext())
        {
			Block block = (Block)iterator.next();

            if (block != null && block.getCreativeTabToDisplayOn() != null)
            {
            	block.getSubBlocks(Item.getItemFromBlock(block), (CreativeTabs)null, stacks);
            }
        }
		reloadControls();
	}
	
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
	
	public static boolean shouldShowItem(boolean onlyBlocks, String search, ItemStack stack)
	{
		if(onlyBlocks && (Block.getBlockFromItem(stack.getItem()) == null || Block.getBlockFromItem(stack.getItem()) instanceof BlockAir))
			return false;
		if(search.equals(""))
			return true;
		return getItemName(stack).toLowerCase().contains(search);
	}
	
	@Override
	public void onOpened()
    {
    	super.onOpened();
    	addListener(this);
    }
	
	@Override
	public void onClosed()
	{
		super.onClosed();
		removeListener(this);
	}
	
	@Override
	public void reloadControls()
	{
		if(stacks != null)
		{
			controls.clear();
			int height = 0;
			GuiLabel label = new GuiLabel("Inventory", 3, height);
			label.width = width-20;
			label.height = 14;
			controls.add(label);
			height += label.height;
			
			int SlotsPerRow = (width-20)/18;
			int count = 0;
			for (int i = 0; i < inv.size(); i++) {
				if(selector.allow(inv.get(i)))
				{
					InventoryBasic basic = new InventoryBasic("", false, 1);
					basic.setInventorySlotContents(0, inv.get(i));
					
					int row = count/SlotsPerRow;
					addControl(new SlotControlNoSync(new SlotPreview(basic, 0, (count-row*SlotsPerRow)*18, height+row*18)).getGuiControl());
					count++;
				}
			}
			height += Math.floor(count/SlotsPerRow+1)*18;
			
			label = new GuiLabel("Items", 3, height);
			label.width = width-20;
			label.height = 14;
			addControl(label);
			height += label.height;
			count = 0;
			for (int i = 0; i < stacks.size(); i++) {
				if(selector.allow(stacks.get(i)))
				{
					InventoryBasic basic = new InventoryBasic("", false, 1);
					basic.setInventorySlotContents(0, stacks.get(i));
					int row = count/SlotsPerRow;
					addControl(new SlotControlNoSync(new SlotPreview(basic, 0, (count-row*SlotsPerRow)*18, height+row*18)).getGuiControl());
					count++;
				}
			}
		}
	}
	
	@CustomEventSubscribe
	public void onLabelClicked(GuiControlClickEvent event)
	{
		if(event.source instanceof GuiSlotControl && event.source.parent == this)
		{
			((GuiInvSelector)comboBox).addAndSelectStack(((GuiSlotControl) event.source).slot.slot.getStack().copy());
			comboBox.closeBox();
		}
	}
	
	@Override
	public boolean mouseScrolled(int posX, int posY, int scrolled){
		this.scrolled -= scrolled*30;
		onScrolled();
		return true;
	}
}
