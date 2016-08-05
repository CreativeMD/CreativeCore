package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.creativecore.common.utils.RenderCubeObject;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

public interface ICreativeRendered {
	
	public ArrayList<RenderCubeObject> getRenderingCubes(IBlockState state, TileEntity te, ItemStack stack);
	
	public default void applyCustomOpenGLHackery(ItemStack stack) {}
	
	public default List<BakedQuad> getCachedModel(EnumFacing facing, BlockRenderLayer layer, IBlockState state, TileEntity te, ItemStack stack)
	{
		return null;
	}
	
	public default void saveCachedModel(EnumFacing facing, BlockRenderLayer layer, List<BakedQuad> cachedQuads, IBlockState state, TileEntity te, ItemStack stack) {}
}