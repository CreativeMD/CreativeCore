package com.creativemd.creativecore.common.world;

import com.creativemd.creativecore.client.rendering.IRenderChunkSupplier;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
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
	public void neighborChanged(BlockPos pos, final Block blockIn, BlockPos fromPos) {
		if (preventNeighborUpdate)
			return;
		if (this.isRemote) {
			IBlockState iblockstate = this.getBlockState(pos);
			
			try {
				iblockstate.neighborChanged(this, pos, blockIn, fromPos);
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
				crashreportcategory.addDetail("Source block type", new ICrashReportDetail<String>() {
					@Override
					public String call() throws Exception {
						try {
							return String.format("ID #%d (%s // %s // %s)", Block.getIdFromBlock(blockIn), blockIn.getUnlocalizedName(), blockIn.getClass().getName(), blockIn.getRegistryName());
						} catch (Throwable var2) {
							return "ID #" + Block.getIdFromBlock(blockIn);
						}
					}
				});
				CrashReportCategory.addBlockInfo(crashreportcategory, pos, iblockstate);
				throw new ReportedException(crashreport);
			}
		} else
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
