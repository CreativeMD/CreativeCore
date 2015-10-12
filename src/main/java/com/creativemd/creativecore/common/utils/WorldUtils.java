package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class WorldUtils {
	
	public static void dropItem(World world, ArrayList<ItemStack> stacks, int x, int y, int z)
	{
		for (int i = 0; i < stacks.size(); i++) {
			dropItem(world, stacks.get(i), x, y, z);
		}
	}
	
	public static void dropItem(World world, ItemStack stack, int x, int y, int z)
	{
		float f = 0.7F;
        double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, stack);
        entityitem.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(entityitem);
	}
	
	public static void dropItem(EntityPlayer player, ItemStack stack)
	{
		dropItem(player.worldObj, stack, (int)player.posX, (int)player.posY, (int)player.posZ);
	}
	
	public static void dropItem(EntityPlayer player, ArrayList<ItemStack> stacks)
	{
		for (int i = 0; i < stacks.size(); i++) {
			dropItem(player, stacks.get(i));
		}
	}
	
}
