package com.creativemd.creativecore.common.world;

import com.creativemd.creativecore.client.rendering.IRenderChunkSupplier;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CreativeWorld extends World implements IOrientatedWorld {
	
	public Entity parent;
	@SideOnly(Side.CLIENT)
	public IRenderChunkSupplier renderChunkSupplier;
	public boolean hasChanged = false;
	
	public boolean preventNeighborUpdate = false;
	
	protected CreativeWorld(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}
	
	@Override
	public void neighborChanged(BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (preventNeighborUpdate)
			return;
		super.neighborChanged(pos, blockIn, fromPos);
	}
	
	@Override
	public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType, boolean updateObservers) {
		if (preventNeighborUpdate)
			return;
		super.notifyNeighborsOfStateChange(pos, blockType, updateObservers);
	}
	
	@Override
	public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
		if (preventNeighborUpdate)
			return;
		super.notifyNeighborsOfStateExcept(pos, blockType, skipSide);
	}
	
	@Override
	public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType, boolean p_175722_3_) {
		if (preventNeighborUpdate)
			return;
		super.notifyNeighborsRespectDebug(pos, blockType, p_175722_3_);
	}
}
