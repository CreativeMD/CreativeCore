package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.IGuiCreator;
import com.creativemd.creativecore.common.packet.OpenGuiPacket;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.TEContainerPacket;
import com.jcraft.jogg.Packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {
	
	public static ArrayList<ContainerSub> openContainers = new ArrayList<ContainerSub>();
	
	private static HashMap<String, CustomGuiHandler> guihandlers = new HashMap<String, CustomGuiHandler>();
	
	public static void registerGuiHandler(String id, CustomGuiHandler handler)
	{
		guihandlers.put(id, handler);
	}
	
	public static CustomGuiHandler getHandler(String id)
	{
		return guihandlers.get(id);
	}
	
	public static void openGui(String id, NBTTagCompound nbt, EntityPlayer player)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			openGui(id, nbt);
		else{
			OpenGuiPacket packet = new OpenGuiPacket(id, nbt);
			PacketHandler.sendPacketToPlayer(packet, (EntityPlayerMP) player);
			packet.executeServer(player);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void openGui(String id, NBTTagCompound nbt)
	{
		OpenGuiPacket packet = new OpenGuiPacket(id, nbt);
		PacketHandler.sendPacketToServer(packet);
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
		/*if(ID >= 2)
		{
			ID -= 2;
			if(guihandlers.size() > ID)
				return new ContainerSub(player, guihandlers.get(ID).getContainer(player, null, world, x, y, z));
		}*/
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
		/*if(ID >= 2)
		{
			ID -= 2;
			if(guihandlers.size() > ID)
				return new GuiContainerSub(player, guihandlers.get(ID).getGui(player, null, null, 0, 0, 0), guihandlers.get(ID).getContainer(player, null, world, x, y, z));
		}*/
		return null;
	}

}
