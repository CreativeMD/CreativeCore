package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtils {
	
	public static void dropItem(World world, ArrayList<ItemStack> stacks, BlockPos pos)
	{
		for (int i = 0; i < stacks.size(); i++) {
			dropItem(world, stacks.get(i), pos);
		}
	}
	
	public static void dropItem(World world, ItemStack stack, BlockPos pos)
	{
		float f = 0.7F;
        double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
        entityitem.setPickupDelay(10);
		world.spawnEntityInWorld(entityitem);
	}
	
	public static void dropItem(EntityPlayer player, ItemStack stack)
	{
		dropItem(player.worldObj, stack, player.getPosition());
	}
	
	public static void dropItem(EntityPlayer player, ArrayList<ItemStack> stacks)
	{
		for (int i = 0; i < stacks.size(); i++) {
			dropItem(player, stacks.get(i));
		}
	}
	
}
