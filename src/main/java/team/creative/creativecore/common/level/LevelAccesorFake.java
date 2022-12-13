package team.creative.creativecore.common.level;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;

public class LevelAccesorFake implements LevelAccessor {
    
    protected Level level;
    protected BlockPos pos;
    protected BlockState state;
    
    public void set(Level level, BlockPos pos, BlockState state) {
        this.level = level;
        this.pos = pos;
        this.state = state;
    }
    
    @Override
    public RegistryAccess registryAccess() {
        return level.registryAccess();
    }
    
    @Override
    public List<Entity> getEntities(Entity p_45936_, AABB p_45937_, Predicate<? super Entity> p_45938_) {
        return level.getEntities(p_45936_, p_45937_, p_45938_);
    }
    
    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> p_151464_, AABB p_151465_, Predicate<? super T> p_151466_) {
        return level.getEntities(p_151464_, p_151465_, p_151466_);
    }
    
    @Override
    public List<? extends Player> players() {
        return level.players();
    }
    
    @Override
    public ChunkAccess getChunk(int p_46823_, int p_46824_, ChunkStatus p_46825_, boolean p_46826_) {
        return level.getChunk(p_46823_, p_46824_, p_46825_, p_46826_);
    }
    
    @Override
    public int getHeight(Types p_46827_, int p_46828_, int p_46829_) {
        return level.getHeight(p_46827_, p_46828_, p_46829_);
    }
    
    @Override
    public int getSkyDarken() {
        return level.getSkyDarken();
    }
    
    @Override
    public BiomeManager getBiomeManager() {
        return level.getBiomeManager();
    }
    
    @Override
    public Holder<Biome> getUncachedNoiseBiome(int p_46809_, int p_46810_, int p_46811_) {
        return level.getUncachedNoiseBiome(p_46809_, p_46810_, p_46811_);
    }
    
    @Override
    public boolean isClientSide() {
        return level.isClientSide;
    }
    
    @Override
    public int getSeaLevel() {
        return level.getSeaLevel();
    }
    
    @Override
    public DimensionType dimensionType() {
        return level.dimensionType();
    }
    
    @Override
    public float getShade(Direction p_45522_, boolean p_45523_) {
        return level.getShade(p_45522_, p_45523_);
    }
    
    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }
    
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        if (pos.equals(this.pos))
            return null;
        return level.getBlockEntity(pos);
    }
    
    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals(this.pos))
            return state;
        return level.getBlockState(pos);
    }
    
    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (pos.equals(this.pos))
            return Fluids.EMPTY.defaultFluidState();
        return level.getFluidState(pos);
    }
    
    @Override
    public WorldBorder getWorldBorder() {
        return level.getWorldBorder();
    }
    
    @Override
    public boolean isStateAtPosition(BlockPos p_46938_, Predicate<BlockState> p_46939_) {
        return level.isStateAtPosition(p_46938_, p_46939_);
    }
    
    @Override
    public boolean isFluidAtPosition(BlockPos p_151584_, Predicate<FluidState> p_151585_) {
        return level.isFluidAtPosition(p_151584_, p_151585_);
    }
    
    @Override
    public boolean setBlock(BlockPos p_46947_, BlockState p_46948_, int p_46949_, int p_46950_) {
        return level.setBlock(p_46947_, p_46948_, p_46950_);
    }
    
    @Override
    public boolean removeBlock(BlockPos p_46951_, boolean p_46952_) {
        return level.removeBlock(p_46951_, p_46952_);
    }
    
    @Override
    public boolean destroyBlock(BlockPos p_46957_, boolean p_46958_, Entity p_46959_, int p_46960_) {
        return level.destroyBlock(p_46957_, p_46958_, p_46959_, p_46960_);
    }
    
    @Override
    public long nextSubTickCount() {
        return level.nextSubTickCount();
    }
    
    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return level.getBlockTicks();
    }
    
    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return level.getFluidTicks();
    }
    
    @Override
    public LevelData getLevelData() {
        return level.getLevelData();
    }
    
    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_46800_) {
        return level.getCurrentDifficultyAt(p_46800_);
    }
    
    @Override
    public MinecraftServer getServer() {
        return level.getServer();
    }
    
    @Override
    public ChunkSource getChunkSource() {
        return level.getChunkSource();
    }
    
    @Override
    public RandomSource getRandom() {
        return level.getRandom();
    }
    
    @Override
    public void playSound(Player p_46775_, BlockPos p_46776_, SoundEvent p_46777_, SoundSource p_46778_, float p_46779_, float p_46780_) {
        level.playSound(p_46775_, p_46776_, p_46777_, p_46778_, p_46779_, p_46780_);
    }
    
    @Override
    public void addParticle(ParticleOptions p_46783_, double p_46784_, double p_46785_, double p_46786_, double p_46787_, double p_46788_, double p_46789_) {
        level.addParticle(p_46783_, p_46784_, p_46785_, p_46786_, p_46787_, p_46788_, p_46789_);
    }
    
    @Override
    public void levelEvent(Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
        level.levelEvent(p_46771_, p_46772_, p_46773_, p_46774_);
    }
    
    @Override
    public void gameEvent(Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
        level.gameEvent(p_151549_, p_151550_, p_151551_);
    }
    
    @Override
    public void gameEvent(GameEvent p_220404_, Vec3 p_220405_, Context p_220406_) {
        level.gameEvent(p_220404_, pos, p_220406_);
    }
    
    @Override
    public FeatureFlagSet enabledFeatures() {
        return level.enabledFeatures();
    }
    
}
