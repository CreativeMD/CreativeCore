package com.creativemd.creativecore.common.container;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSub extends Container{
	
	public SubContainer subContainer;
	
	public ContainerSub(EntityPlayer player, SubContainer subContainer)
	{
		this.subContainer = subContainer;
		subContainer.container = this;
		subContainer.slots.addAll(subContainer.getSlots(player));
		inventorySlots.clear();
		for (int i = 0; i < subContainer.slots.size(); i++) {
			addSlotToContainer(subContainer.slots.get(i));
		}
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
		subContainer.onUpdate();
    }
	
	@Override
	public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        subContainer.onGuiClosed(player);
    }
	
	/**Vanilla method fixed not took care of getSlotStockLimit**/
	@Override
	protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_)
    {
        boolean flag1 = false;
        int k = p_75135_2_;

        if (p_75135_4_)
        {
            k = p_75135_3_ - 1;
        }

        Slot slot;
        ItemStack itemstack1;
        if (p_75135_1_.isStackable())
        {
            while (p_75135_1_.stackSize > 0 && (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_))
            {
                slot = (Slot)this.inventorySlots.get(k);
                itemstack1 = slot.getStack();
                
                int stackSize = p_75135_1_.getMaxStackSize();
                if(slot.getSlotStackLimit() < stackSize)
                	stackSize = slot.getSlotStackLimit();
                
                if (itemstack1 != null && itemstack1.getItem() == p_75135_1_.getItem() && (!p_75135_1_.getHasSubtypes() || p_75135_1_.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(p_75135_1_, itemstack1))
                {
                    int l = itemstack1.stackSize + p_75135_1_.stackSize;
                    
                    if (l <= stackSize)
                    {
                        p_75135_1_.stackSize = 0;
                        itemstack1.stackSize = l;
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                    else if (itemstack1.stackSize < stackSize)
                    {
                        p_75135_1_.stackSize -= stackSize - itemstack1.stackSize;
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

        if (p_75135_1_.stackSize > 0)
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

                if (itemstack1 == null && slot.isItemValid(p_75135_1_))
                {
                	ItemStack newStack = p_75135_1_.copy();
                	int rest = 0;
                	if(slot.getSlotStackLimit() < newStack.stackSize)
                	{
                		rest = newStack.stackSize - slot.getSlotStackLimit();
                		newStack.stackSize = slot.getSlotStackLimit();
                	}
                    slot.putStack(newStack);                    
                    slot.onSlotChanged();
                    p_75135_1_.stackSize = rest;
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
