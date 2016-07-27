package com.creativemd.creativecore.client.rendering.model;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CreativeBlockRenderHelper {
	
	public static HashMap<ResourceLocation, Block> blocks = new HashMap<>();
	
	public static void registerCreativeRenderedBlock(Block block)
	{
		blocks.put(block.getRegistryName(), block);
	}
	
}
