package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public interface IExtendedCreativeRendered extends ICreativeRendered {
	
	public List<BakedQuad> getSpecialBakedQuads(IBlockState state, TileEntity te, EnumFacing side, long rand, ItemStack stack);

}
