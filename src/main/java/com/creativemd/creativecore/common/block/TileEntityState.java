package com.creativemd.creativecore.common.block;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;

public class TileEntityState extends StateImplementation {
	
	public final TileEntity te;
	
	public TileEntityState(IBlockState state, TileEntity te) {
		super(state.getBlock(), state.getProperties());
		this.te = te;
	}

}
