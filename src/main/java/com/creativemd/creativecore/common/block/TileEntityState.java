package com.creativemd.creativecore.common.block;

import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public class TileEntityState extends StateImplementation {

	public final TileEntity te;

	public TileEntityState(IBlockState state, TileEntity te) {
		super(state.getBlock(), state.getProperties());
		this.te = te;
	}

}
