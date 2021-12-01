package team.creative.creativecore.common.level;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.client.render.level.IRenderChunkSupplier;
import team.creative.creativecore.common.level.listener.LevelBoundsListener;
import team.creative.creativecore.common.level.system.BlockUpdateLevelSystem;

public abstract class CreativeLevel extends Level implements IOrientatedLevel {
    
    public Entity parent;
    final EntityTickList tickingEntities = new EntityTickList();
    private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager<>(Entity.class, new CreativeLevel.EntityCallbacks());
    @OnlyIn(Dist.CLIENT)
    public IRenderChunkSupplier renderChunkSupplier;
    private final Map<String, MapItemSavedData> mapData = Maps.newHashMap();
    private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectOpenHashMap<>();
    private final List<AbstractClientPlayer> players = Lists.newArrayList();
    private final FakeChunkCache chunkSource;
    
    public final BlockUpdateLevelSystem blockUpdate = new BlockUpdateLevelSystem(this);
    
    public boolean hasChanged = false;
    public boolean preventNeighborUpdate = false;
    
    protected CreativeLevel(WritableLevelData worldInfo, int radius, Supplier<ProfilerFiller> supplier, boolean client, boolean debug, long seed) {
        super(worldInfo, CreativeCore.FAKE_DIMENSION_NAME, CreativeCore.FAKE_DIMENSION, supplier, client, debug, seed);
        this.chunkSource = new FakeChunkCache(this, radius);
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
    public ChunkSource getChunkSource() {
        return chunkSource;
    }
    
    public void unload(LevelChunk chunk) {
        chunk.clearAllBlockEntities();
        this.chunkSource.getLightEngine().enableLightSources(chunk.getPos(), false);
    }
    
    public void onChunkLoaded(ChunkPos pos) {}
    
    @Override
    public List<? extends Player> players() {
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
    public LevelEntityGetter<Entity> getEntities() {
        return this.entityStorage.getEntityGetter();
    }
    
    public Iterable<Entity> loadedEntities() {
        return getEntities().getAll();
    }
    
    @Override
    public String gatherChunkSourceStats() {
        return "Chunks[C] W: " + this.chunkSource.gatherStats() + " E: " + this.entityStorage.gatherStats();
    }
    
    @Override
    public MapItemSavedData getMapData(String data) {
        return this.mapData.get(data);
    }
    
    @Override
    public void setMapData(String id, MapItemSavedData data) {
        this.mapData.put(id, data);
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
    
    final class EntityCallbacks implements LevelCallback<Entity> {
        @Override
        public void onCreated(Entity p_171696_) {}
        
        @Override
        public void onDestroyed(Entity p_171700_) {}
        
        @Override
        public void onTickingStart(Entity p_171704_) {
            CreativeLevel.this.tickingEntities.add(p_171704_);
        }
        
        @Override
        public void onTickingEnd(Entity p_171708_) {
            CreativeLevel.this.tickingEntities.remove(p_171708_);
        }
        
        @Override
        public void onTrackingStart(Entity p_171712_) {
            if (p_171712_ instanceof AbstractClientPlayer) {
                CreativeLevel.this.players.add((AbstractClientPlayer) p_171712_);
            }
            
        }
        
        @Override
        public void onTrackingEnd(Entity p_171716_) {
            p_171716_.unRide();
            CreativeLevel.this.players.remove(p_171716_);
            
            p_171716_.onRemovedFromWorld();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityLeaveWorldEvent(p_171716_, CreativeLevel.this));
        }
    }
    
}
