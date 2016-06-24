package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;

import com.creativemd.creativecore.common.utils.CubeObject;

import net.minecraft.block.state.IBlockState;

public interface ICreativeRendered {
	
	public ArrayList<CubeObject> getRenderingCubes(IBlockState state);
	
}
