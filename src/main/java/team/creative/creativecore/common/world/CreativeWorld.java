package team.creative.creativecore.common.world;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.client.render.world.IRenderChunkSupplier;

public abstract class CreativeWorld extends World implements IOrientatedWorld {
    
    public Entity parent;
    @OnlyIn(value = Dist.CLIENT)
    public IRenderChunkSupplier renderChunkSupplier;
    private final Map<String, MapData> mapData = Maps.newHashMap();
    private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectOpenHashMap<>();
    private final List<AbstractClientPlayerEntity> players = Lists.newArrayList();
    private final ChunkProviderFake chunkSource;
    
    public boolean hasChanged = false;
    public boolean preventNeighborUpdate = false;
    
    protected CreativeWorld(ISpawnWorldInfo worldInfo, int radius, Supplier<IProfiler> supplier, boolean client, boolean debug, long seed) {
        super(worldInfo, CreativeCore.FAKE_DIMENSION_NAME, CreativeCore.FAKE_DIMENSION, supplier, client, debug, seed);
        this.chunkSource = new ChunkProviderFake(this, radius);
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
                CrashReportCategory.populateBlockDetails(crashreportcategory, pos, blockstate);
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
    public ITickList<Block> getBlockTicks() {
        return EmptyTickList.empty();
    }
    
    @Override
    public ITickList<Fluid> getLiquidTicks() {
        return EmptyTickList.empty();
    }
    
    @Override
    public AbstractChunkProvider getChunkSource() {
        return chunkSource;
    }
    
    public void unload(Chunk chunk) {
        this.blockEntitiesToUnload.addAll(chunk.getBlockEntities().values());
        this.chunkSource.getLightEngine().enableLightSources(chunk.getPos(), false);
    }
    
    public void onChunkLoaded(int x, int z) {}
    
    @Override
    public List<? extends PlayerEntity> players() {
        return players;
    }
    
    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState state, BlockState p_184138_3_, int p_184138_4_) {
        this.hasChanged = true;
    }
    
    @Override
    public Entity getEntity(int id) {
        return this.entitiesById.get(id);
    }
    
    @Override
    public MapData getMapData(String p_217406_1_) {
        return this.mapData.get(p_217406_1_);
    }
    
    @Override
    public void setMapData(MapData p_217399_1_) {
        this.mapData.put(p_217399_1_.getId(), p_217399_1_);
    }
    
    @Override
    public int getFreeMapId() {
        return 0;
    }
    
    @Override
    public void destroyBlockProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {}
    
}
