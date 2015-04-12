package com.creativemd.creativecore.common.gui;

import com.creativemd.creativecore.common.container.SubContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public interface IGuiCreator {
	
	/**If it's not a stack: stack == null, else world == null; x = 0; y = 0; z = 0*/
	public SubGui getGui(EntityPlayer player, ItemStack stack, World world, int x, int y, int z);
	
	/**If it's not a stack: stack == null, else world == null; x = 0; y = 0; z = 0*/
	public SubContainer getContainer(EntityPlayer player, ItemStack stack, World world, int x, int y, int z);
}
