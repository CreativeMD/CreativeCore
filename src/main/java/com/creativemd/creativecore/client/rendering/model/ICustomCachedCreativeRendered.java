package com.creativemd.creativecore.client.rendering.model;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.BlockInfo;

public interface ICustomCachedCreativeRendered extends ICreativeRendered {
	
	public QuadCache[] getCustomCachedQuads(BlockRenderLayer layer, EnumFacing facing, TileEntity te, ItemStack stack);
	
	public void saveCachedQuads(QuadCache[] quads, BlockRenderLayer layer, EnumFacing facing, TileEntity te, ItemStack stack);
	
}
