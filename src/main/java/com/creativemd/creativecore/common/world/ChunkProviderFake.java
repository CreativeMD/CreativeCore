package com.creativemd.creativecore.common.world;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ChunkProviderFake implements IChunkProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Set<Long> droppedChunksSet = Sets.<Long>newHashSet();
    public final IChunkGenerator chunkGenerator;
    public final IChunkLoader chunkLoader;
    public final Long2ObjectMap<Chunk> id2ChunkMap = new Long2ObjectOpenHashMap(4);
    public final CreativeWorld worldObj;
    private Set<Long> loadingChunks = com.google.common.collect.Sets.newHashSet();
    
    public ChunkProviderFake(CreativeWorld worldObjIn, IChunkLoader chunkLoaderIn, IChunkGenerator chunkGeneratorIn) {
        this.worldObj = worldObjIn;
        this.chunkLoader = chunkLoaderIn;
        this.chunkGenerator = chunkGeneratorIn;
    }
    
    public Collection<Chunk> getLoadedChunks() {
        return this.id2ChunkMap.values();
    }
    
    /** Unloads a chunk */
    public void unload(Chunk chunkIn) {
        if (this.worldObj.provider.canDropChunk(chunkIn.x, chunkIn.z)) {
            this.droppedChunksSet.add(Long.valueOf(ChunkPos.asLong(chunkIn.x, chunkIn.z)));
            if (markLoaded())
                chunkIn.markLoaded(false);
        }
    }
    
    public boolean markLoaded() {
        return true;
    }
    
    /** marks all chunks for unload, ignoring those near the spawn */
    public void unloadAllChunks() {
        for (Chunk chunk : this.id2ChunkMap.values()) {
            this.unload(chunk);
        }
    }
    
    @Override
    @Nullable
    public Chunk getLoadedChunk(int x, int z) {
        long i = ChunkPos.asLong(x, z);
        Chunk chunk = this.id2ChunkMap.get(i);
        
        if (worldObj.isRemote && chunk != null && markLoaded()) {
            //chunk.markLoaded(false);
        }
        return chunk;
    }
    
    @Nullable
    public Chunk loadChunk(int x, int z) {
        return loadChunk(x, z, null);
    }
    
    @Nullable
    public Chunk loadChunk(int x, int z, Runnable runnable) {
        Chunk chunk = this.getLoadedChunk(x, z);
        if (chunk == null) {
            long pos = ChunkPos.asLong(x, z);
            chunk = this.loadChunkFromFile(x, z);
            this.id2ChunkMap.put(ChunkPos.asLong(x, z), chunk);
        }
        /* chunk = net.minecraftforge.common.ForgeChunkManager.fetchDormantChunk(pos,
         * this.worldObj); if (chunk != null || !(this.chunkLoader instanceof
         * net.minecraft.world.chunk.storage.AnvilChunkLoader)) { if
         * (!loadingChunks.add(pos)) net.minecraftforge.fml.common.FMLLog.
         * bigWarning("There is an attempt to load a chunk (%d,%d) in dimension %d that is already being loaded. This will cause weird chunk breakages."
         * , x, z, this.worldObj.provider.getDimension()); if (chunk == null) chunk =
         * this.loadChunkFromFile(x, z);
         * 
         * if (chunk != null) { this.id2ChunkMap.put(ChunkPos.chunkXZ2Int(x, z), chunk);
         * chunk.onChunkLoad(); chunk.populateChunk(this, this.chunkGenerator); }
         * 
         * loadingChunks.remove(pos); } else {
         * net.minecraft.world.chunk.storage.AnvilChunkLoader loader =
         * (net.minecraft.world.chunk.storage.AnvilChunkLoader) this.chunkLoader; if
         * (runnable == null) chunk =
         * net.minecraftforge.common.chunkio.ChunkIOExecutor.syncChunkLoad(this.
         * worldObj, loader, this, x, z); else if (loader.chunkExists(this.worldObj, x,
         * z)) { // We can only use the async queue for already generated chunks
         * net.minecraftforge.common.chunkio.ChunkIOExecutor.queueChunkLoad(this.
         * worldObj, loader, this, x, z, runnable); return null; } } }
         * 
         * // If we didn't load the chunk async and have a callback run it now if
         * (runnable != null) runnable.run(); */
        return chunk;
    }
    
    @Override
    public Chunk provideChunk(int x, int z) {
        Chunk chunk = this.loadChunk(x, z);
        
        if (chunk == null) {
            long i = ChunkPos.asLong(x, z);
            
            try {
                chunk = this.chunkGenerator.generateChunk(x, z);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                crashreportcategory.addCrashSection("Location", String.format("%d,%d", new Object[] { Integer.valueOf(x), Integer.valueOf(z) }));
                crashreportcategory.addCrashSection("Position hash", Long.valueOf(i));
                crashreportcategory.addCrashSection("Generator", this.chunkGenerator);
                throw new ReportedException(crashreport);
            }
            
            this.id2ChunkMap.put(i, chunk);
            chunk.onLoad();
            chunk.populate(this, this.chunkGenerator);
        }
        
        return chunk;
    }
    
    private static Field loadedChunk = ReflectionHelper.findField(Chunk.class, new String[] { "loaded", "field_76636_d" });
    
    @Nullable
    private Chunk loadChunkFromFile(int x, int z) {
        try {
            Chunk chunk = this.chunkLoader.loadChunk(this.worldObj, x, z);
            
            if (chunk != null) {
                chunk.setLastSaveTime(this.worldObj.getTotalWorldTime());
                // this.chunkGenerator.recreateStructures(chunk, x, z);
            }
            //chunk.onLoad();
            loadedChunk.setBoolean(chunk, true);
            return chunk;
        } catch (Exception exception) {
            LOGGER.error("Couldn\'t load chunk", exception);
            return null;
        }
    }
    
    private void saveChunkExtraData(Chunk chunkIn) {
        try {
            this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
        } catch (Exception exception) {
            LOGGER.error("Couldn\'t save entities", exception);
        }
    }
    
    private void saveChunkData(Chunk chunkIn) {
        try {
            chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
            this.chunkLoader.saveChunk(this.worldObj, chunkIn);
        } catch (IOException ioexception) {
            LOGGER.error("Couldn\'t save chunk", ioexception);
        } catch (MinecraftException minecraftexception) {
            LOGGER.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", minecraftexception);
        }
    }
    
    public boolean saveChunks(boolean p_186027_1_) {
        int i = 0;
        List<Chunk> list = Lists.newArrayList(this.id2ChunkMap.values());
        
        for (int j = 0; j < ((List) list).size(); ++j) {
            Chunk chunk = list.get(j);
            
            if (p_186027_1_) {
                this.saveChunkExtraData(chunk);
            }
            
            if (chunk.needsSaving(p_186027_1_)) {
                this.saveChunkData(chunk);
                chunk.setModified(false);
                ++i;
                
                if (i == 24 && !p_186027_1_) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /** Unloads chunks that are marked to be unloaded. This is not guaranteed to
     * unload every such chunk. */
    public boolean unloadQueuedChunks() {
        /* if (!this.worldObj.disableLevelSaving) { if
         * (!this.droppedChunksSet.isEmpty()) { for (ChunkPos forced :
         * this.worldObj.getPersistentChunks().keySet()) {
         * this.droppedChunksSet.remove(ChunkPos.chunkXZ2Int(forced.chunkXPos,
         * forced.chunkZPos)); }
         * 
         * Iterator<Long> iterator = this.droppedChunksSet.iterator();
         * 
         * for (int i = 0; i < 100 && iterator.hasNext(); iterator.remove()) { Long
         * olong = (Long)iterator.next(); Chunk chunk =
         * (Chunk)this.id2ChunkMap.get(olong);
         * 
         * if (chunk != null && chunk.unloaded) { chunk.onChunkUnload();
         * this.saveChunkData(chunk); this.saveChunkExtraData(chunk);
         * this.id2ChunkMap.remove(olong); ++i;
         * net.minecraftforge.common.ForgeChunkManager.putDormantChunk(ChunkPos.
         * chunkXZ2Int(chunk.xPosition, chunk.zPosition), chunk); if (id2ChunkMap.size()
         * == 0 &&
         * net.minecraftforge.common.ForgeChunkManager.getPersistentChunksFor(this.
         * worldObj).size() == 0 &&
         * !this.worldObj.provider.getDimensionType().shouldLoadSpawn()){
         * net.minecraftforge.common.DimensionManager.unloadWorld(this.worldObj.provider
         * .getDimension()); break; } } } }
         * 
         * this.chunkLoader.chunkTick(); } */
        
        return false;
    }
    
    /** Returns if the IChunkProvider supports saving. */
    public boolean canSave() {
        return false; // !this.worldObj.disableLevelSaving;
    }
    
    /** Converts the instance data to a readable string. */
    @Override
    public String makeString() {
        return "ServerChunkCache: " + this.id2ChunkMap.size() + " Drop: " + this.droppedChunksSet.size();
    }
    
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return this.chunkGenerator.getPossibleCreatures(creatureType, pos);
    }
    
    @Nullable
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_) {
        return this.chunkGenerator.getNearestStructurePos(worldIn, structureName, position, p_180513_4_);
    }
    
    public int getLoadedChunkCount() {
        return this.id2ChunkMap.size();
    }
    
    /** Checks to see if a chunk exists at x, z */
    public boolean chunkExists(int x, int z) {
        return this.id2ChunkMap.containsKey(ChunkPos.asLong(x, z));
    }
    
    @Override
    public boolean isChunkGeneratedAt(int p_191062_1_, int p_191062_2_) {
        return this.id2ChunkMap.containsKey(ChunkPos.asLong(p_191062_1_, p_191062_2_)) || this.chunkLoader.isChunkGeneratedAt(p_191062_1_, p_191062_2_);
    }
    
    @Override
    public boolean tick() {
        return false;
    }
}