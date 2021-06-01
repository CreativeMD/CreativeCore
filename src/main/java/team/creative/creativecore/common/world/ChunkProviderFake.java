package team.creative.creativecore.common.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkProviderFake extends AbstractChunkProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Chunk emptyChunk;
    private final WorldLightManager lightEngine;
    private volatile ChunkProviderFake.ChunkArray storage;
    public final CreativeWorld world;
    
    public ChunkProviderFake(CreativeWorld world, int radius) {
        this.world = world;
        this.emptyChunk = new EmptyChunk(world, new ChunkPos(0, 0));
        this.lightEngine = new WorldLightManager(this, true, world.dimensionType().hasSkyLight());
        this.storage = new ChunkProviderFake.ChunkArray(calculateStorageRange(radius));
    }
    
    private static boolean isValidChunk(@Nullable Chunk p_217249_0_, int p_217249_1_, int p_217249_2_) {
        if (p_217249_0_ == null) {
            return false;
        } else {
            ChunkPos chunkpos = p_217249_0_.getPos();
            return chunkpos.x == p_217249_1_ && chunkpos.z == p_217249_2_;
        }
    }
    
    public void drop(int p_73234_1_, int p_73234_2_) {
        if (this.storage.inRange(p_73234_1_, p_73234_2_)) {
            int i = this.storage.getIndex(p_73234_1_, p_73234_2_);
            Chunk chunk = this.storage.getChunk(i);
            if (isValidChunk(chunk, p_73234_1_, p_73234_2_)) {
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(chunk));
                this.storage.replace(i, chunk, (Chunk) null);
            }
            
        }
    }
    
    @Override
    @Nullable
    public Chunk getChunk(int p_212849_1_, int p_212849_2_, ChunkStatus p_212849_3_, boolean p_212849_4_) {
        if (this.storage.inRange(p_212849_1_, p_212849_2_)) {
            Chunk chunk = this.storage.getChunk(this.storage.getIndex(p_212849_1_, p_212849_2_));
            if (isValidChunk(chunk, p_212849_1_, p_212849_2_)) {
                return chunk;
            }
        }
        
        return p_212849_4_ ? this.emptyChunk : null;
    }
    
    @Override
    public IBlockReader getLevel() {
        return this.world;
    }
    
    @Nullable
    public Chunk replaceWithPacketData(int p_228313_1_, int p_228313_2_, @Nullable BiomeContainer p_228313_3_, PacketBuffer p_228313_4_, CompoundNBT p_228313_5_, int p_228313_6_, boolean p_228313_7_) {
        if (!this.storage.inRange(p_228313_1_, p_228313_2_)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", p_228313_1_, p_228313_2_);
            return null;
        } else {
            int i = this.storage.getIndex(p_228313_1_, p_228313_2_);
            Chunk chunk = this.storage.chunks.get(i);
            if (!p_228313_7_ && isValidChunk(chunk, p_228313_1_, p_228313_2_)) {
                chunk.replaceWithPacketData(p_228313_3_, p_228313_4_, p_228313_5_, p_228313_6_);
            } else {
                if (p_228313_3_ == null) {
                    LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", p_228313_1_, p_228313_2_);
                    return null;
                }
                
                chunk = new Chunk(this.world, new ChunkPos(p_228313_1_, p_228313_2_), p_228313_3_);
                chunk.replaceWithPacketData(p_228313_3_, p_228313_4_, p_228313_5_, p_228313_6_);
                this.storage.replace(i, chunk);
            }
            
            ChunkSection[] achunksection = chunk.getSections();
            WorldLightManager worldlightmanager = this.getLightEngine();
            worldlightmanager.enableLightSources(new ChunkPos(p_228313_1_, p_228313_2_), true);
            
            for (int j = 0; j < achunksection.length; ++j) {
                ChunkSection chunksection = achunksection[j];
                worldlightmanager.updateSectionStatus(SectionPos.of(p_228313_1_, j, p_228313_2_), ChunkSection.isEmpty(chunksection));
            }
            
            this.world.onChunkLoaded(p_228313_1_, p_228313_2_);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
            return chunk;
        }
    }
    
    public void tick(BooleanSupplier p_217207_1_) {}
    
    public void updateViewCenter(int p_217251_1_, int p_217251_2_) {
        this.storage.viewCenterX = p_217251_1_;
        this.storage.viewCenterZ = p_217251_2_;
    }
    
    public void updateViewRadius(int p_217248_1_) {
        int i = this.storage.chunkRadius;
        int j = calculateStorageRange(p_217248_1_);
        if (i != j) {
            ChunkProviderFake.ChunkArray clientchunkprovider$chunkarray = new ChunkProviderFake.ChunkArray(j);
            clientchunkprovider$chunkarray.viewCenterX = this.storage.viewCenterX;
            clientchunkprovider$chunkarray.viewCenterZ = this.storage.viewCenterZ;
            
            for (int k = 0; k < this.storage.chunks.length(); ++k) {
                Chunk chunk = this.storage.chunks.get(k);
                if (chunk != null) {
                    ChunkPos chunkpos = chunk.getPos();
                    if (clientchunkprovider$chunkarray.inRange(chunkpos.x, chunkpos.z)) {
                        clientchunkprovider$chunkarray.replace(clientchunkprovider$chunkarray.getIndex(chunkpos.x, chunkpos.z), chunk);
                    }
                }
            }
            
            this.storage = clientchunkprovider$chunkarray;
        }
        
    }
    
    private static int calculateStorageRange(int p_217254_0_) {
        return Math.max(2, p_217254_0_) + 3;
    }
    
    @Override
    public String gatherStats() {
        return "Client Chunk Cache: " + this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
    }
    
    public int getLoadedChunksCount() {
        return this.storage.chunkCount;
    }
    
    @Override
    public void onLightUpdate(LightType p_217201_1_, SectionPos p_217201_2_) {
        Minecraft.getInstance().levelRenderer.setSectionDirty(p_217201_2_.x(), p_217201_2_.y(), p_217201_2_.z());
    }
    
    @Override
    public boolean isTickingChunk(BlockPos p_222866_1_) {
        return this.hasChunk(p_222866_1_.getX() >> 4, p_222866_1_.getZ() >> 4);
    }
    
    @Override
    public boolean isEntityTickingChunk(ChunkPos p_222865_1_) {
        return this.hasChunk(p_222865_1_.x, p_222865_1_.z);
    }
    
    @Override
    public boolean isEntityTickingChunk(Entity p_217204_1_) {
        return this.hasChunk(MathHelper.floor(p_217204_1_.getX()) >> 4, MathHelper.floor(p_217204_1_.getZ()) >> 4);
    }
    
    @Override
    public WorldLightManager getLightEngine() {
        return this.lightEngine;
    }
    
    @OnlyIn(Dist.CLIENT)
    final class ChunkArray {
        private final AtomicReferenceArray<Chunk> chunks;
        private final int chunkRadius;
        private final int viewRange;
        private volatile int viewCenterX;
        private volatile int viewCenterZ;
        private int chunkCount;
        
        private ChunkArray(int p_i50568_2_) {
            this.chunkRadius = p_i50568_2_;
            this.viewRange = p_i50568_2_ * 2 + 1;
            this.chunks = new AtomicReferenceArray<>(this.viewRange * this.viewRange);
        }
        
        private int getIndex(int p_217191_1_, int p_217191_2_) {
            return Math.floorMod(p_217191_2_, this.viewRange) * this.viewRange + Math.floorMod(p_217191_1_, this.viewRange);
        }
        
        protected void replace(int p_217181_1_, @Nullable Chunk p_217181_2_) {
            Chunk chunk = this.chunks.getAndSet(p_217181_1_, p_217181_2_);
            if (chunk != null) {
                --this.chunkCount;
                ChunkProviderFake.this.world.unload(chunk);
            }
            
            if (p_217181_2_ != null) {
                ++this.chunkCount;
            }
            
        }
        
        protected Chunk replace(int p_217190_1_, Chunk p_217190_2_, @Nullable Chunk p_217190_3_) {
            if (this.chunks.compareAndSet(p_217190_1_, p_217190_2_, p_217190_3_) && p_217190_3_ == null) {
                --this.chunkCount;
            }
            
            ChunkProviderFake.this.world.unload(p_217190_2_);
            return p_217190_2_;
        }
        
        private boolean inRange(int p_217183_1_, int p_217183_2_) {
            return Math.abs(p_217183_1_ - this.viewCenterX) <= this.chunkRadius && Math.abs(p_217183_2_ - this.viewCenterZ) <= this.chunkRadius;
        }
        
        @Nullable
        protected Chunk getChunk(int p_217192_1_) {
            return this.chunks.get(p_217192_1_);
        }
    }
}