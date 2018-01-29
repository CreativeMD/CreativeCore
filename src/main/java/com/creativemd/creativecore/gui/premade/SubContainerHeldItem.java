package com.creativemd.creativecore.gui.premade;

import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.controls.container.SlotControl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class SubContainerHeldItem extends SubContainer {
	
	public ItemStack stack;
	public int currentIndex;

	public SubContainerHeldItem(EntityPlayer player) {
		super(player);
		this.currentIndex = player.inventory.currentItem;
		this.stack = player.getHeldItemMainhand();
	}
	
	@Override
	public void addPlayerSlotsToContainer(EntityPlayer player)
	{
		addPlayerSlotsToContainer(player, currentIndex);
	}
	
	@Override
	public void addPlayerSlotsToContainer(EntityPlayer player, int x, int y)
	{
		addPlayerSlotsToContainer(player, x, y, currentIndex);
	}
	
	
}
