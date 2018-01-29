package com.creativemd.creativecore.gui.controls.container;

import javax.annotation.Nullable;

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
		lastSended = slot.getStack().copy();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiControl createGuiControl() {
		GuiControl control = new GuiSlotControl(slot.xPos, slot.yPos, this);
		control.enabled = this.enabled;
		//control.rotation = 45;
		return control;
	}
	
	public ItemStack lastSended = ItemStack.EMPTY;
	
	@Override
	public void onTick()
	{
		if(!ItemStack.areItemStacksEqual(lastSended, slot.getStack())) // || (lastSended != null && slot.getHasStack() && lastSended.stackSize != slot.getStack().stackSize))
		{
			sendUpdate();
			raiseEvent(new SlotChangeEvent(this));
			
		}
	}
	
	public int getItemStackLimit(ItemStack stack)
	{
		return stack.getMaxStackSize();
	}
	
	public int getStackLimit(Slot slot, ItemStack stack)
	{
		return Math.min(slot.getSlotStackLimit(), getItemStackLimit(stack));
	}
	
	@Override
	public void writeToNBTUpdate(NBTTagCompound nbt) {
		ItemStack stack = slot.getStack();
		if(slot.getHasStack())
		{
			stack.writeToNBT(nbt);
			if(stack.getCount() > Byte.MAX_VALUE)
				nbt.setInteger("realCount", stack.getCount());
			lastSended = stack.copy();
		}else
			lastSended = ItemStack.EMPTY;
		NBTTagCompound dragSlot = new NBTTagCompound();
		if(!getPlayer().inventory.getItemStack().isEmpty())
			getPlayer().inventory.getItemStack().writeToNBT(dragSlot);
		nbt.setTag("drag", dragSlot);
	}

	@Override
	public void onPacketReceive(NBTTagCompound nbt) {
		switch(nbt.getInteger("type"))
		{
		/**Update*/
		case 0:
			ItemStack stack = ItemStack.EMPTY;
			if(nbt.hasKey("id"))
			{
				stack = new ItemStack(nbt);
				if(nbt.hasKey("realCount"))
					stack.setCount(nbt.getInteger("realCount"));
			}
			slot.putStack(stack);
			if(!ItemStack.areItemStacksEqual(lastSended, slot.getStack()))
				raiseEvent(new SlotChangeEvent(this));
			lastSended = stack;		
			ItemStack dragStack = ItemStack.EMPTY;
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
				slot.putStack(ItemStack.EMPTY);
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
	
	public boolean canAddItemToSlot(@Nullable Slot slotIn, ItemStack stack, boolean stackSizeMatters)
    {
        boolean flag = slotIn == null || !slotIn.getHasStack();
        return !flag && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack) ? slotIn.getStack().getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= getItemStackLimit(stack) : flag;
    }
	
	public ItemStack putItemInSlot(Slot slot, ItemStack stack)
	{
		boolean canStack = canAddItemToSlot(slot, stack, true);
		int availableStackSize = getStackLimit(slot, stack);
		if(canStack && slot.isItemValid(stack))
		{
			int neededStackSize = stack.getCount();
			if(slot.getHasStack())
				availableStackSize -= slot.getStack().getCount();
			
			int additionalStackSize = Math.min(availableStackSize, neededStackSize);
			
			if(additionalStackSize > 0)
			{
				ItemStack inSlot = stack.copy();
				inSlot.setCount(additionalStackSize);;
				if(slot.getHasStack())
					inSlot.grow(slot.getStack().getCount());
				slot.putStack(inSlot);
				
				stack.shrink(additionalStackSize);
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
			boolean canStack = canAddItemToSlot(slot, hand, true);
			int stackSize = getStackLimit(slot, hand);
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
					inventoryplayer.setItemStack(ItemStack.EMPTY);
			}else if(slotItem.getCount() <= 64){
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
	
	public boolean canMergeIntoInventory(Slot mergeSlot)
	{
		return mergeSlot.inventory != slot.inventory;
	}
	
	public void mergeToOtherInventory(ItemStack toTransfer, boolean useEmptySlot)
	{
		for (int i = 0; i < getParent().controls.size(); i++) {
			if(getParent().controls.get(i) instanceof SlotControl)
			{
				Slot mergeSlot = ((SlotControl)getParent().controls.get(i)).slot;
				if(canMergeIntoInventory(mergeSlot) && (useEmptySlot || mergeSlot.getHasStack()))
				{
					toTransfer = ((SlotControl)getParent().controls.get(i)).putItemInSlot(mergeSlot, toTransfer);
					if(toTransfer.isEmpty())
						return ;
				}
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
				int stackSizeLeft = Math.min(amount, stack.getCount());
				for (int i = 0; i < getParent().controls.size(); i++) {
					if(getParent().controls.get(i) instanceof SlotControl)
					{
						Slot mergeSlot = ((SlotControl)getParent().controls.get(i)).slot;
						if(mergeSlot.inventory != slot.inventory)
						{
							boolean canStack = canAddItemToSlot(mergeSlot, stack, true);
							if(canStack && mergeSlot.getHasStack() && !mergeSlot.getStack().isEmpty())
							{
								int transfer = Math.min(stackSizeLeft, ((SlotControl) getParent().controls.get(i)).getStackLimit(slot, stack));
								stackSizeLeft -= transfer;
								stack.grow(transfer);
								mergeSlot.getStack().shrink(transfer);
								if(mergeSlot.getStack().isEmpty())
									mergeSlot.putStack(ItemStack.EMPTY);
								if(stackSizeLeft <= 0)
									return ;
							}
						}
					}
				}
			}else{
				transferIntoOtherInventory(amount);
			}
		}
	}
	
	public void transferIntoOtherInventory(int amount)
	{
		ItemStack stack = slot.getStack();
		if(amount > stack.getCount())
			amount = stack.getCount();
		ItemStack copy = stack.copy();
		copy.setCount(amount);
		int minAmount = stack.getCount()-amount;
		
		mergeToOtherInventory(copy, false);
		if(!copy.isEmpty())
			mergeToOtherInventory(copy, true);
		
		if(copy.isEmpty())
			stack.setCount(minAmount);
		else
			stack.shrink(amount-copy.getCount());
	}
	
	public void takeStack(boolean leftClick, InventoryPlayer inventoryplayer)
	{
		ItemStack hand = getPlayer().inventory.getItemStack();
		ItemStack slotItem = slot.getStack();
		if(leftClick)
		{
			int stackSize = Math.min(Math.min(slotItem.getCount(), slotItem.getMaxStackSize()), slotItem.getCount());
			ItemStack newHand = slotItem.copy();
			newHand.setCount(stackSize);
			inventoryplayer.setItemStack(newHand);
			slotItem.shrink(stackSize);
			if(slotItem.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			slot.onTake(getPlayer(), inventoryplayer.getItemStack());
		}else{
			int amount = (slotItem.getCount() + 1) / 2;
			hand = slot.decrStackSize(amount);
			inventoryplayer.setItemStack(hand);
			
			if (slotItem.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			
			slot.onTake(getPlayer(), inventoryplayer.getItemStack());
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
					if(!hand.isEmpty())
					{
						switchItems();
					}else if(slot.getHasStack()) {
						takeStack(true, inventoryplayer);
					}
				}
			}
		}else if(mouseButton == 1){
			if(!hand.isEmpty())
			{
				if(slot.isItemValid(hand))
				{
					boolean canStack = canAddItemToSlot(slot, hand, true);
					if(canStack)
					{
						int stacksize = 1;
						if(slot.getHasStack())
							stacksize += slotItem.getCount();
						stacksize = Math.min(stacksize, getStackLimit(slot, hand));
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
				takeStack(false, inventoryplayer);
			}
		}else if(mouseButton == 2){
			if(hand.isEmpty() && slot.getHasStack() && getPlayer().capabilities.isCreativeMode)
			{
				ItemStack stack = slotItem.copy();
				stack.setCount(getItemStackLimit(stack));
				inventoryplayer.setItemStack(stack);
			}
		}else if(mouseButton == 3){
			if(scrolled == 1 ||slot.canTakeStack(getPlayer()))
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
			int stackSize = control.getStackLimit(control.slot, stack);
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
