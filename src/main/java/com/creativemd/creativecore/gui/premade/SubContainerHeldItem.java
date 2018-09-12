package com.creativemd.creativecore.gui.premade;

import com.creativemd.creativecore.gui.container.SubContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class SubContainerHeldItem extends SubContainer {

	public ItemStack stack;
	public int currentIndex;

	public SubContainerHeldItem(EntityPlayer player) {
		super(player);
		this.currentIndex = player.inventory.currentItem;
		this.stack = player.getHeldItemMainhand();
	}

	@Override
	public void addPlayerSlotsToContainer(EntityPlayer player) {
		addPlayerSlotsToContainer(player, currentIndex);
	}

	@Override
	public void addPlayerSlotsToContainer(EntityPlayer player, int x, int y) {
		addPlayerSlotsToContainer(player, x, y, currentIndex);
	}

}
