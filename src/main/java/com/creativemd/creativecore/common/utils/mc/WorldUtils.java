package com.creativemd.creativecore.common.utils.mc;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldUtils {
	
	public static boolean checkIfChunkExists(Chunk chunk)
	{
		if(chunk == null)
			return false;
		if(chunk.getWorld().isRemote)
			return checkIfChunkExistsClient(chunk);
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	private static boolean checkIfChunkExistsClient(Chunk chunk)
	{
		return !(chunk instanceof EmptyChunk);
	}
	
	public static void dropItem(World world, ArrayList<ItemStack> stacks, BlockPos pos)
	{
		for (int i = 0; i < stacks.size(); i++) {
			dropItem(world, stacks.get(i), pos);
		}
	}
	
	public static void dropItem(World world, ItemStack stack, BlockPos pos)
	{
		if(stack == null)
			return ;
		float f = 0.7F;
        double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
        entityitem.setPickupDelay(10);
		world.spawnEntity(entityitem);
	}
	
	public static void dropItem(EntityPlayer player, ItemStack stack)
	{
		if(stack != null)
			dropItem(player.world, stack, player.getPosition());
	}
	
	public static void dropItem(EntityPlayer player, ArrayList<ItemStack> stacks)
	{
		for (int i = 0; i < stacks.size(); i++) {
			dropItem(player, stacks.get(i));
		}
	}
	
	public static boolean isMainThread(World world)
	{
		if(world.isRemote)
			return Minecraft.getMinecraft().isCallingFromMinecraftThread();
		else
			return world.getMinecraftServer().isCallingFromMinecraftThread();
	}
	
}
