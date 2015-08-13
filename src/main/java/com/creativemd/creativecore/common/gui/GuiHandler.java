package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.IGuiCreator;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.TEContainerPacket;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	public static ArrayList<ContainerSub> openContainers = new ArrayList<ContainerSub>();
	
	private static ArrayList<IGuiCreator> guihandlers = new ArrayList<IGuiCreator>();
	
	public static int registerGuiHandler(IGuiCreator creator)
	{
		guihandlers.add(creator);
		return guihandlers.size()+1;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID)
		{
		case 0: //It's a block
			Block block = world.getBlock(x, y, z);
			ContainerSub container = new ContainerSub(player, ((IGuiCreator) block).getContainer(player, null, world, x, y, z));
			container.coord = new ChunkCoordinates(x, y, z);
			openContainers.add(container);
			return container;
		case 1: //It's an item
			ItemStack stack = player.getHeldItem();
			if(stack != null && stack.getItem() instanceof IGuiCreator)
				return new ContainerSub(player, ((IGuiCreator) stack.getItem()).getContainer(player, stack, null, 0, 0, 0));
		}
		if(ID >= 2)
		{
			ID -= 2;
			if(guihandlers.size() > ID)
				return new ContainerSub(player, guihandlers.get(ID).getContainer(player, null, world, x, y, z));
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
		if(ID >= 2)
		{
			ID -= 2;
			if(guihandlers.size() > ID)
				return new GuiContainerSub(player, guihandlers.get(ID).getGui(player, null, null, 0, 0, 0), guihandlers.get(ID).getContainer(player, null, world, x, y, z));
		}
		return null;
	}

}
