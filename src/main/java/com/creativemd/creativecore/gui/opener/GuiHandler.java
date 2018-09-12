package com.creativemd.creativecore.gui.opener;

import java.util.ArrayList;
import java.util.HashMap;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.gui.OpenGuiPacket;
import com.creativemd.creativecore.gui.mc.ContainerSub;
import com.creativemd.creativecore.gui.mc.GuiContainerSub;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {
	
	public static ArrayList<ContainerSub> openContainers = new ArrayList<ContainerSub>();
	
	private static HashMap<String, CustomGuiHandler> guihandlers = new HashMap<String, CustomGuiHandler>();
	
	public static void registerGuiHandler(String id, CustomGuiHandler handler) {
		guihandlers.put(id, handler);
	}
	
	public static CustomGuiHandler getHandler(String id) {
		return guihandlers.get(id);
	}
	
	public static void openGuiItem(EntityPlayer player, World world) {
		if (!world.isRemote)
			((EntityPlayerMP) player).openGui(CreativeCore.instance, 1, world, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
	
	public static void openGui(EntityPlayer player, World world, BlockPos pos) {
		if (!world.isRemote)
			((EntityPlayerMP) player).openGui(CreativeCore.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static void openGui(String id, NBTTagCompound nbt, EntityPlayer player) {
		if (player.getEntityWorld().isRemote)
			openGui(id, nbt);
		else {
			OpenGuiPacket packet = new OpenGuiPacket(id, nbt);
			PacketHandler.sendPacketToPlayer(packet, (EntityPlayerMP) player);
			packet.executeServer(player);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void openGui(String id, NBTTagCompound nbt) {
		OpenGuiPacket packet = new OpenGuiPacket(id, nbt);
		PacketHandler.sendPacketToServer(packet);
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case 0: // It's a block
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof IGuiCreator) {
				ContainerSub container = new ContainerSub(player, ((IGuiCreator) state.getBlock()).getContainer(player, null, world, pos, state));
				container.coord = pos;
				openContainers.add(container);
				return container;
			}
		case 1: // It's an item
			ItemStack stack = player.getHeldItemMainhand();
			if (stack == null || !(stack.getItem() instanceof IGuiCreator))
				stack = player.getHeldItemOffhand();
			if (stack != null && stack.getItem() instanceof IGuiCreator)
				return new ContainerSub(player, ((IGuiCreator) stack.getItem()).getContainer(player, stack, null, null, null));
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case 0: // It's a block
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof IGuiCreator)
				return new GuiContainerSub(player, ((IGuiCreator) state.getBlock()).getGui(player, null, world, pos, state), ((IGuiCreator) state.getBlock()).getContainer(player, null, world, pos, state));
		case 1: // It's an item
			ItemStack stack = player.getHeldItemMainhand();
			if (stack == null || !(stack.getItem() instanceof IGuiCreator))
				stack = player.getHeldItemOffhand();
			if (stack != null && stack.getItem() instanceof IGuiCreator)
				return new GuiContainerSub(player, ((IGuiCreator) stack.getItem()).getGui(player, stack, null, null, null), ((IGuiCreator) stack.getItem()).getContainer(player, stack, null, null, null));
		}
		return null;
	}
	
}
