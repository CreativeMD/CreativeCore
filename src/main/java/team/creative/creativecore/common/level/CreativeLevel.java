package team.creative.creativecore.common.level;

import java.util.function.Supplier;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.level.listener.LevelBoundsListener;
import team.creative.creativecore.common.level.system.BlockUpdateLevelSystem;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;

public abstract class CreativeLevel extends Level implements IOrientatedLevel {
    
    public Entity holder;
    public IVecOrigin origin;
    
    private final FakeChunkCache chunkSource;
    
    public final BlockUpdateLevelSystem blockUpdate = new BlockUpdateLevelSystem(this);
    
    public boolean hasChanged = false;
    public boolean preventNeighborUpdate = false;
    
    protected CreativeLevel(WritableLevelData worldInfo, int radius, Supplier<ProfilerFiller> supplier, boolean client, boolean debug, long seed) {
        super(worldInfo, CreativeCore.FAKE_DIMENSION_NAME, CreativeCore.FAKE_DIMENSION, supplier, client, debug, seed);
        this.chunkSource = new FakeChunkCache(this, radius);
    }
    
    @Override
    public Entity getHolder() {
        return holder;
    }
    
    @Override
    public void setHolder(Entity entity) {
        this.holder = entity;
    }
    
    public void registerLevelBoundListener(LevelBoundsListener listener) {
        this.blockUpdate.registerLevelBoundListener(listener);
    }
    
    @Override
    public void neighborChanged(BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (preventNeighborUpdate)
            return;
        if (this.isClientSide) {
            BlockState blockstate = this.getBlockState(pos);
            
            try {
                blockstate.neighborChanged(this, pos, blockIn, fromPos, false);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while updating neighbours");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Block being updated");
                crashreportcategory.setDetail("Source block type", () -> {
                    try {
                        return String.format("ID #%s (%s // %s)", blockIn.getRegistryName(), blockIn.getDescriptionId(), blockIn.getClass().getCanonicalName());
                    } catch (Throwable throwable1) {
                        return "ID #" + blockIn.getRegistryName();
                    }
                });
                CrashReportCategory.populateBlockDetails(crashreportcategory, this, pos, blockstate);
                throw new ReportedException(crashreport);
            }
        } else
            super.neighborChanged(pos, blockIn, fromPos);
    }
    
    @Override
    public void updateNeighborsAtExceptFromFacing(BlockPos pos, Block block, Direction facing) {
        if (preventNeighborUpdate)
            return;
        super.updateNeighborsAtExceptFromFacing(pos, block, facing);
    }
    
    @Override
    public void updateNeighborsAt(BlockPos pos, Block block) {
        if (preventNeighborUpdate)
            return;
        super.updateNeighborsAt(pos, block);
    }
    
    public BlockPos transformToRealWorld(BlockPos pos) {
        return getOrigin().transformPointToWorld(pos);
    }
    
    @Override
    public FakeChunkCache getChunkSource() {
        return chunkSource;
    }
    
    public void unload(LevelChunk chunk) {
        chunk.clearAllBlockEntities();
        this.chunkSource.getLightEngine().enableLightSources(chunk.getPos(), false);
    }
    
    public void onChunkLoaded(ChunkPos pos) {}
    
    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState state, BlockState p_184138_3_, int p_184138_4_) {
        this.hasChanged = true;
    }
    
    public Iterable<Entity> loadedEntities() {
        return getEntities().getAll();
    }
    
    @Override
    public int getFreeMapId() {
        return 0;
    }
    
    @Override
    public void destroyBlockProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {}
    
    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }
    
    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }
    
}
