package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;

import com.creativemd.creativecore.common.utils.CubeObject;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public interface ICreativeRendered {
	
	public ArrayList<CubeObject> getRenderingCubes(IBlockState state, TileEntity te);
	
}