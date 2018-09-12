package com.creativemd.creativecore.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public class IBlockAccessFake implements IBlockAccess {

	public IBlockAccess parent;
	public BlockPos pos;
	public IBlockState fakeState;

	public IBlockAccessFake() {

	}

	public void set(IBlockAccess world, BlockPos pos, IBlockState fakeState) {
		this.parent = world;
		this.pos = pos;
		this.fakeState = fakeState;
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		if (pos.equals(this.pos))
			return null;
		return parent.getTileEntity(pos);
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return parent.getCombinedLight(pos, lightValue);
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		if (pos.equals(this.pos))
			return fakeState;
		return parent.getBlockState(pos);
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		if (pos.equals(this.pos))
			return fakeState.getBlock().isAir(fakeState, this, pos);
		return parent.isAirBlock(pos);
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return parent.getBiome(pos);
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return parent.getStrongPower(pos, direction);
	}

	@Override
	public WorldType getWorldType() {
		return parent.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		if (pos.equals(this.pos))
			return fakeState.isSideSolid(this, pos, side);
		return parent.isSideSolid(pos, side, _default);
	}

}
