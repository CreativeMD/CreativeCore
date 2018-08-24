package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.creativemd.creativecore.common.utils.type.HashMapList;
import com.creativemd.creativecore.gui.controls.container.SlotControlNoSync;
import com.creativemd.creativecore.gui.controls.container.client.GuiSlotControl;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBoxExtension;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.slots.SlotPreview;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class GuiStackSelectorExtension extends GuiComboBoxExtension {
	
	public GuiStackSelectorExtension(String name, EntityPlayer player, int x, int y, int width, int height, GuiStackSelector comboBox)
	{
		super(name, comboBox, x, y, width, height, new ArrayList<>());
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
		if(comboBox == null)
			return ;
		HashMapList<String, ItemStack> stacks = ((GuiStackSelector) comboBox).getStacks();
		
		int height = 0;
		controls.clear();
		
		for (Entry<String, ArrayList<ItemStack>> entry : stacks.entrySet()) {
			GuiLabel label = new GuiLabel(translate(entry.getKey()), 3, height);
			label.width = width-20;
			label.height = 14;
			controls.add(label);
			height += label.height;
			
			int SlotsPerRow = (width-20)/18;
			
			InventoryBasic basic = new InventoryBasic(entry.getKey(), false, entry.getValue().size());
			int i = 0;
			for(ItemStack stack : entry.getValue())
			{
				basic.setInventorySlotContents(i, stack);
				
				int row = i/SlotsPerRow;
				addControl(new SlotControlNoSync(new SlotPreview(basic, i, (i-row*SlotsPerRow)*18, height+row*18)).getGuiControl());
				i++;
			}
			
			height += Math.ceil(i/(double) SlotsPerRow)*18;
		}
	}
	
	@CustomEventSubscribe
	public void onLabelClicked(GuiControlClickEvent event)
	{
		if(event.source instanceof GuiSlotControl && event.source.parent == this)
		{
			((GuiStackSelector) comboBox).setSelected(((GuiSlotControl) event.source).slot.slot.getStack());
			comboBox.closeBox();
		}
	}
	
	@Override
	public boolean mouseScrolled(int posX, int posY, int scrolled){
		/*this.scrolled -= scrolled*30;
		onScrolled();
		return true;*/
		return super.mouseScrolled(posX, posY, scrolled);
	}
	

}
