package com.creativemd.creativecore.common.utils.mc;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldUtils {
	
	public static boolean checkIfChunkExists(Chunk chunk) {
		if (chunk == null)
			return false;
		if (chunk.getWorld().isRemote)
			return checkIfChunkExistsClient(chunk);
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	private static boolean checkIfChunkExistsClient(Chunk chunk) {
		return !(chunk instanceof EmptyChunk);
	}
	
	public static void dropItem(World world, List<ItemStack> stacks, BlockPos pos) {
		for (int i = 0; i < stacks.size(); i++) {
			dropItem(world, stacks.get(i), pos);
		}
	}
	
	public static void dropItem(World world, ItemStack stack, BlockPos pos) {
		if (stack == null)
			return;
		float f = 0.7F;
		double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
		double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
		double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
		EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
		entityitem.setPickupDelay(10);
		world.spawnEntity(entityitem);
	}
	
	public static void dropItem(EntityPlayer player, ItemStack stack) {
		if (stack != null)
			dropItem(player.world, stack, player.getPosition());
	}
	
	public static void dropItem(EntityPlayer player, List<ItemStack> stacks) {
		for (int i = 0; i < stacks.size(); i++) {
			dropItem(player, stacks.get(i));
		}
	}
	
}
