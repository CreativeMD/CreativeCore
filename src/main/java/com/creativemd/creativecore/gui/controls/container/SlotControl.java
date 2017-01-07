package com.creativemd.creativecore.gui.controls.container;

import com.creativemd.creativecore.gui.ContainerControl;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.controls.container.client.GuiSlotControl;
import com.creativemd.creativecore.gui.event.container.SlotChangeEvent;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotControl extends ContainerControl{
	
	public Slot slot;
	
	public SlotControl(Slot slot)
	{
		super(slot.inventory.getName() + slot.getSlotIndex());
		this.slot = slot;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiControl createGuiControl() {
		GuiControl control = new GuiSlotControl(slot.xPos, slot.yPos, this);
		//control.rotation = 45;
		return control;
	}
	
	public ItemStack lastSended;
	
	@Override
	public void onTick()
	{
		if(!ItemStack.areItemStacksEqual(lastSended, slot.getStack())) // || (lastSended != null && slot.getHasStack() && lastSended.stackSize != slot.getStack().stackSize))
		{
			raiseEvent(new SlotChangeEvent(this));
			sendUpdate();
		}
	}
	
	@Override
	public void writeToNBTUpdate(NBTTagCompound nbt) {
		if(slot.getHasStack())
			slot.getStack().writeToNBT(nbt);
		if(slot.getHasStack())
			lastSended = slot.getStack().copy();
		else
			lastSended = null;
		NBTTagCompound dragSlot = new NBTTagCompound();
		if(getPlayer().inventory.getItemStack() != null)
			getPlayer().inventory.getItemStack().writeToNBT(dragSlot);
		nbt.setTag("drag", dragSlot);
	}

	@Override
	public void onPacketReceive(NBTTagCompound nbt) {
		switch(nbt.getInteger("type"))
		{
		/**Update*/
		case 0:
			ItemStack stack = null;
			if(nbt.hasKey("id"))
				stack = new ItemStack(nbt);
			slot.putStack(stack);
			if(!ItemStack.areItemStacksEqual(lastSended, slot.getStack()))
				raiseEvent(new SlotChangeEvent(this));
			lastSended = stack;		
			ItemStack dragStack = null;
			if(nbt.hasKey("drag"))
				dragStack = new ItemStack(nbt.getCompoundTag("drag"));
			getPlayer().inventory.setItemStack(dragStack);
			break;
		/**Clicked*/
		case 1: 
			onSlotClicked(nbt.getInteger("button"), nbt.getBoolean("shift"), nbt.getInteger("scrolled"));
			break;
		case 2:
			dropItem(nbt.getBoolean("ctrl"));
			break;
		case 3:
			splitStack(nbt.getIntArray("slots"), new ItemStack(nbt), nbt.getBoolean("right"));
			break;
		}
	}
	
	public void dropItem(boolean ctrl)
	{
		if(slot.getHasStack())
		{
			ItemStack drop = slot.getStack().copy();
			if(ctrl)
			{
				slot.putStack(null);
			}else{
				drop.setCount(1);
				ItemStack newStack = slot.getStack();
				newStack.shrink(1);
				if(newStack.isEmpty())
					slot.putStack(ItemStack.EMPTY);
				else
					slot.putStack(newStack);
			}
			getPlayer().dropItem(drop, true);
			slot.onTake(getPlayer(), drop);
		}
	}
	
	public static ItemStack putItemInSlot(Slot slot, ItemStack stack)
	{
		boolean canStack = Container.canAddItemToSlot(slot, stack, true);
		int stackSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
		if(canStack && slot.isItemValid(stack))
		{
			int neededStackSize = stack.getCount();
			if(slot.getHasStack())
				stackSize -= slot.getStack().getCount();
			
			stackSize = Math.min(stackSize, neededStackSize);
			
			if(stackSize > 0)
			{
				ItemStack inSlot = stack.copy();
				inSlot.setCount(stackSize);;
				if(slot.getHasStack())
					inSlot.grow(slot.getStack().getCount());
				slot.putStack(inSlot);
				
				stack.shrink(stackSize);
				if(stack.isEmpty())
					return ItemStack.EMPTY;
			}
		}
		return stack;
	}
	
	public void switchItems()
	{
		ItemStack hand = getPlayer().inventory.getItemStack();
		InventoryPlayer inventoryplayer = getPlayer().inventory;
		ItemStack slotItem = slot.getStack();
		
		if(slot.isItemValid(hand))
		{
			boolean canStack = Container.canAddItemToSlot(slot, hand, true);
			int stackSize = Math.min(slot.getSlotStackLimit(), hand.getMaxStackSize());
			if(canStack && (!slot.getHasStack() || slot.canTakeStack(getPlayer())))
			{
				int neededStackSize = hand.getCount();
				if(slot.getHasStack())
					stackSize -= slotItem.getCount();
				
				stackSize = Math.min(stackSize, neededStackSize);
				ItemStack inSlot = hand.copy();
				
				inSlot.setCount(stackSize);
				if(slot.getHasStack())
					inSlot.grow(slotItem.getCount());
				
				slot.putStack(inSlot);
				int left = neededStackSize - stackSize;
				if(left > 0)
					hand.setCount(left);
				else
					inventoryplayer.setItemStack(null);
			}else{
				int left = hand.getCount() - stackSize;
				if(left > 1)
					getPlayer().dropItem(slotItem.copy(), true);
				ItemStack inSlot = hand.copy();
				inSlot.setCount(Math.min(stackSize, hand.getCount()));
				slot.putStack(inSlot);
				hand.shrink(Math.min(stackSize, hand.getCount()));
				if(hand.isEmpty())
					inventoryplayer.setItemStack(slotItem.copy());
			}
		}
	}
	
	public void transferIntoOtherInventory(boolean reverse, int amount)
	{
		if(slot.getHasStack())
		{
			ItemStack stack = slot.getStack();
			
			if(reverse)
			{
				int stackSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
				stackSize -= stack.getCount();
				if(stackSize > 0)
				{
					for (int i = 0; i < getParent().controls.size(); i++) {
						if(getParent().controls.get(i) instanceof SlotControl)
						{
							Slot mergeSlot = ((SlotControl)getParent().controls.get(i)).slot;
							if(mergeSlot.inventory != slot.inventory)
							{
								boolean canStack = Container.canAddItemToSlot(slot, stack, true);
								if(canStack && mergeSlot.getHasStack() && !mergeSlot.getStack().isEmpty())
								{
									stack.grow(1);
									mergeSlot.getStack().shrink(1);
									if(mergeSlot.getStack().isEmpty())
										mergeSlot.putStack(ItemStack.EMPTY);
									return ;
								}
							}
						}
					}
				}
			}else{
				if(amount > stack.getCount())
					amount = stack.getCount();
				ItemStack copy = stack.copy();
				copy.setCount(amount);
				int minAmount = stack.getCount()-amount;
				
				for (int i = 0; i < getParent().controls.size(); i++) {
					if(getParent().controls.get(i) instanceof SlotControl)
					{
						Slot mergeSlot = ((SlotControl)getParent().controls.get(i)).slot;
						if(mergeSlot.inventory != slot.inventory)
						{
							
							copy = putItemInSlot(mergeSlot, copy);
							if(copy != null)
								stack.shrink(amount-copy.getCount());
							else
								stack.setCount(minAmount);
							
							if(slot.getStack().isEmpty())
							{
								slot.putStack(ItemStack.EMPTY);
								return ;
							}
							if(copy == ItemStack.EMPTY)
								return ;
						}
					}
				}
			}
		}
	}
	
	public void onSlotClicked(int mouseButton, boolean shift, int scrolled)
	{
		ItemStack hand = getPlayer().inventory.getItemStack();
		InventoryPlayer inventoryplayer = getPlayer().inventory;
		ItemStack slotItem = slot.getStack();
		boolean isWheel = false;
		if(mouseButton == 0)
		{
			if(slot.canTakeStack(getPlayer()))
			{
				if(shift){
					transferIntoOtherInventory(false, 64);
				}else{
					if(hand != null)
					{
						switchItems();
					}else if(slot.getHasStack()) {
						inventoryplayer.setItemStack(slotItem.copy());
						slot.putStack(null);
						slot.onTake(getPlayer(), inventoryplayer.getItemStack());
					}
				}
			}
		}else if(mouseButton == 1){
			if(hand != null)
			{
				if(slot.isItemValid(hand))
				{
					boolean canStack = Container.canAddItemToSlot(slot, hand, true);
					if(canStack)
					{
						int stacksize = 1;
						if(slot.getHasStack())
							stacksize += slotItem.getCount();
						stacksize = Math.min(stacksize, Math.min(slot.getSlotStackLimit(), hand.getMaxStackSize()));
						if((slot.getHasStack() && stacksize > slotItem.getCount()) || (!slot.getHasStack() && stacksize > 0))
						{
							ItemStack inSlot = hand.copy();
							inSlot.setCount(stacksize);
							slot.putStack(inSlot);
							hand.shrink(1);
							if(hand.isEmpty())
								inventoryplayer.setItemStack(ItemStack.EMPTY);
						}
					}else{
						switchItems();
					}
				}
				//!slot.getHasStack() || slot.canTakeStack(parent.player)
			}else if(slot.getHasStack() && slot.canTakeStack(getPlayer())){
					int amount = (slotItem.getCount() + 1) / 2;
					hand = slot.decrStackSize(amount);
					inventoryplayer.setItemStack(hand);
					
					if (slotItem.isEmpty())
						slot.putStack(ItemStack.EMPTY);
					
					slot.onTake(getPlayer(), inventoryplayer.getItemStack());
			}
		}else if(mouseButton == 2){
			if(hand == null && slot.getHasStack() && getPlayer().capabilities.isCreativeMode)
			{
				ItemStack stack = slotItem.copy();
				stack.setCount(stack.getMaxStackSize());
				inventoryplayer.setItemStack(stack);
			}
		}else if(mouseButton == 3){
			if(slot.canTakeStack(getPlayer()))
				transferIntoOtherInventory(scrolled == -1, 1);
		}
	}
	
	public void splitStack(int[] slots, ItemStack stack, boolean isRightClick)
	{
		stack = stack.copy();
		int StackPerSlot = MathHelper.floor((float)stack.getCount() / (float)slots.length);
		if(isRightClick)
			StackPerSlot = 1;
		for (int i = 0; i < slots.length; i++)
		{
			SlotControl control = (SlotControl) getParent().controls.get(slots[i]);
			int stackSize = Math.min(control.slot.getSlotStackLimit(), stack.getMaxStackSize());
			if(control.slot.getHasStack())
				stackSize -= control.slot.getStack().getCount();
			
			int use = Math.min(StackPerSlot, Math.min(stackSize, stack.getCount()));
			
			if(use > 0)
			{
				int size = use;
				if(control.slot.getHasStack())
					size += control.slot.getStack().getCount();
				
				ItemStack inSlot = stack.copy();
				inSlot.setCount(size);
				control.slot.putStack(inSlot);
			}
			stack.shrink(use);
		}
		if(stack.isEmpty())
			getPlayer().inventory.setItemStack(ItemStack.EMPTY);
		else
			getPlayer().inventory.setItemStack(stack.copy());
	}
	
	
}
