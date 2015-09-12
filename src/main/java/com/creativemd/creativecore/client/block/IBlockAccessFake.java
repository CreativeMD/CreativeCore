package com.creativemd.creativecore.client.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class IBlockAccessFake implements IBlockAccess{
	
	public IBlockAccess world;
	public int overrideMeta = -1;
	public TileEntity overrideTE = null;
	
	public IBlockAccessFake(IBlockAccess world)
	{
		this.world = world;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return world.getBlock(x, y, z);
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		if(overrideTE != null)
			return overrideTE;
		return world.getTileEntity(x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int side) {
		return world.getLightBrightnessForSkyBlocks(x, y, z, side);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		if(overrideMeta != -1)
			return overrideMeta;
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
		return world.isBlockProvidingPowerTo(x, y, z, side);
	}

	@Override
	public boolean isAirBlock(int x, int y, int z) {
		return world.isAirBlock(x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return world.getBiomeGenForCoords(x, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight() {
		return world.getHeight();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean extendedLevelsInChunkCache() {
		return world.extendedLevelsInChunkCache();
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side,
			boolean _default) {
		return world.isSideSolid(x, y, z, side, _default);
	}
}
