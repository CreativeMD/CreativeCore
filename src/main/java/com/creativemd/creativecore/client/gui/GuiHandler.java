package com.creativemd.creativecore.client.gui;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.IGuiCreator;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID)
		{
		case 0: //It's a block
			Block block = world.getBlock(x, y, z);
			if(block instanceof IGuiCreator)
				return new ContainerSub(player, ((IGuiCreator) block).getContainer(player, null, world, x, y, z));
		case 1: //It's an item
			ItemStack stack = player.getHeldItem();
			if(stack != null && stack.getItem() instanceof IGuiCreator)
				return new ContainerSub(player, ((IGuiCreator) stack.getItem()).getContainer(player, stack, null, 0, 0, 0));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID)
		{
		case 0: //It's a block
			Block block = world.getBlock(x, y, z);
			if(block instanceof IGuiCreator)
				return new GuiContainerSub(player, ((IGuiCreator) block).getGui(player, null, world, x, y, z), ((IGuiCreator) block).getContainer(player, null, world, x, y, z));
		case 1: //It's an item
			ItemStack stack = player.getHeldItem();
			if(stack != null && stack.getItem() instanceof IGuiCreator)
				return new GuiContainerSub(player, ((IGuiCreator) stack.getItem()).getGui(player, stack, null, 0, 0, 0), ((IGuiCreator) stack.getItem()).getContainer(player, stack, null, 0, 0, 0));
		}
		return null;
	}

}
