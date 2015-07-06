package com.creativemd.creativecore.common.container;

import java.util.ArrayList;

import com.creativemd.creativecore.common.container.slot.ContainerControl;
import com.creativemd.creativecore.common.container.slot.SlotImage;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.controls.GuiControl;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSub extends Container{
	
	public SubContainer subContainer;
	
	@SideOnly(Side.CLIENT)
	public GuiContainerSub gui;
	
	public ArrayList<ContainerControl> controls;
	
	public ContainerSub(EntityPlayer player, SubContainer subContainer)
	{
		this.subContainer = subContainer;
		subContainer.container = this;
		
		controls = subContainer.getControls();
		for (int i = 0; i < controls.size(); i++)
		{
			controls.get(i).parent = subContainer;
			controls.get(i).setID(i);
		}
		
		/*subContainer.slots.addAll(subContainer.getSlots(player));
		inventorySlots.clear();
		for (int i = 0; i < subContainer.slots.size(); i++) {
			addSlotToContainer(subContainer.slots.get(i));
		}*/
		subContainer.onGuiOpened();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
		ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        int playerInv = -1;
        for (int i = 0; i < inventorySlots.size(); i++) {
			if(inventorySlots.get(i) instanceof Slot && ((Slot) inventorySlots.get(i)).inventory instanceof InventoryPlayer && playerInv == -1)
				playerInv = i;
        }
        if (slot != null && slot.getHasStack() && playerInv != -1)
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index >= playerInv)
            {
                if(!this.mergeItemStack(itemstack1, 0, playerInv, false))
            		return null;
            }
            else if (!this.mergeItemStack(itemstack1, playerInv, inventorySlots.size(), false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	
	@Override
	public void detectAndSendChanges()
    {
		super.detectAndSendChanges();
		//subContainer.onSlotChange();
		subContainer.onUpdate();
    }
	
	@Override
	public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        subContainer.onGuiClosed();
    }
	
	/**Vanilla method fixed not took care of getSlotStockLimit**/
	@Override
	protected boolean mergeItemStack(ItemStack stack, int p_75135_2_, int p_75135_3_, boolean p_75135_4_)
    {
        boolean flag1 = false;
        int k = p_75135_2_;

        if (p_75135_4_)
        {
            k = p_75135_3_ - 1;
        }

        Slot slot;
        ItemStack itemstack1;
        if (stack.isStackable())
        {
            while (stack.stackSize > 0 && (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_))
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();
                
                int stackSize = stack.getMaxStackSize();
                if(slot.getSlotStackLimit() < stackSize)
                	stackSize = slot.getSlotStackLimit();
                
                if (!(slot instanceof SlotImage) && itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1) && slot.isItemValid(itemstack1))
                {
                    int l = itemstack1.stackSize + stack.stackSize;
                    
                    if (l <= stackSize)
                    {
                        stack.stackSize = 0;
                        itemstack1.stackSize = l;
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                    else if (itemstack1.stackSize < stackSize)
                    {
                        stack.stackSize -= stackSize - itemstack1.stackSize;
                        itemstack1.stackSize = stackSize;
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }

                if (p_75135_4_)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        if (stack.stackSize > 0)
        {
            if (p_75135_4_)
            {
                k = p_75135_3_ - 1;
            }
            else
            {
                k = p_75135_2_;
            }

            while (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_)
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (!(slot instanceof SlotImage) && itemstack1 == null && slot.isItemValid(stack))
                {
                	ItemStack newStack = stack.copy();
                	int rest = 0;
                	if(slot.getSlotStackLimit() < newStack.stackSize)
                	{
                		rest = newStack.stackSize - slot.getSlotStackLimit();
                		newStack.stackSize = slot.getSlotStackLimit();
                	}
                    slot.putStack(newStack);                    
                    slot.onSlotChanged();
                    stack.stackSize = rest;
                    flag1 = true;
                    break;
                }

                if (p_75135_4_)
                {
                    --k;
                }
                else
                {
                    ++k;
                }
            }
        }

        return flag1;
    }
}
