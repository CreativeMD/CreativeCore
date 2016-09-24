package com.creativemd.creativecore.client.rendering.model;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.BlockInfo;

public interface ICustomCachedCreativeRendered extends ICreativeRendered {
	
	public QuadCache[] getCustomCachedQuads(BlockInfo info, BlockRenderLayer layer, EnumFacing facing, TileEntity te, ItemStack stack);
	
	public void saveCachedQuads(QuadCache[] quads, BlockInfo info, BlockRenderLayer layer, EnumFacing facing, TileEntity te, ItemStack stack);
	
}
