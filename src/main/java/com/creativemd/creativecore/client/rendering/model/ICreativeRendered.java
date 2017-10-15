package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.client.rendering.RenderCubeObject;
import com.creativemd.creativecore.common.utils.CubeObject;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICreativeRendered {
	
	@SideOnly(Side.CLIENT)
	public List<? extends RenderCubeObject> getRenderingCubes(IBlockState state, TileEntity te, ItemStack stack);
	
	@SideOnly(Side.CLIENT)
	public default void applyCustomOpenGLHackery(ItemStack stack, TransformType cameraTransformType) {}
	
	@SideOnly(Side.CLIENT)
	public default List<BakedQuad> getCachedModel(EnumFacing facing, BlockRenderLayer layer, IBlockState state, TileEntity te, ItemStack stack, boolean threaded)
	{
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public default void saveCachedModel(EnumFacing facing, BlockRenderLayer layer, List<BakedQuad> cachedQuads, IBlockState state, TileEntity te, ItemStack stack, boolean threaded) {}
}