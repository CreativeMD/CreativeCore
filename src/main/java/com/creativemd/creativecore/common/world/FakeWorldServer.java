package com.creativemd.creativecore.common.world;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.common.block.SpongeBlockSnapshot;
import org.spongepowered.common.config.SpongeConfig;
import org.spongepowered.common.config.type.WorldConfig;
import org.spongepowered.common.interfaces.world.IMixinWorldServer;
import org.spongepowered.common.world.gen.SpongeChunkGenerator;
import org.spongepowered.common.world.gen.SpongeWorldGenerator;

import co.aikar.timings.WorldTimingsHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(modid = "sponge", iface = "org.spongepowered.common.interfaces.world.IMixinWorldServer")
public class FakeWorldServer extends FakeWorld implements IMixinWorldServer {
	
	protected FakeWorldServer(MinecraftServer server, WorldInfo info, WorldProvider provider, boolean isRemote) {
		super(server, info, provider, isRemote);
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return new ChunkProviderFakeServer(this, this.saveHandler.getChunkLoader(provider), provider.createChunkGenerator());
	}
	
	@Override
	public long getWeatherStartTime() {
		throw new RuntimeException();
	}
	
	@Override
	public void setWeatherStartTime(long weatherStartTime) {
		throw new RuntimeException();
	}
	
	@Override
	public void setCallingWorldEvent(boolean flag) {
		throw new RuntimeException();
	}
	
	@Override
	public EntityPlayer getClosestPlayerToEntityWhoAffectsSpawning(Entity entity, double d1tance) {
		throw new RuntimeException();
	}
	
	@Override
	public EntityPlayer getClosestPlayerWhoAffectsSpawning(double x, double y, double z, double distance) {
		throw new RuntimeException();
	}
	
	@Override
	public SpongeConfig<?> getActiveConfig() {
		throw new RuntimeException();
	}
	
	@Override
	public SpongeConfig<WorldConfig> getWorldConfig() {
		throw new RuntimeException();
	}
	
	@Override
	public void setActiveConfig(SpongeConfig<?> config) {
		throw new RuntimeException();
	}
	
	@Override
	public Integer getDimensionId() {
		throw new RuntimeException();
	}
	
	@Override
	public void updateWorldGenerator() {
		throw new RuntimeException();
	}
	
	@Override
	public void updateRotation(Entity entityIn) {
		throw new RuntimeException();
	}
	
	@Override
	public void spongeNotifyNeighborsPostBlockChange(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		throw new RuntimeException();
	}
	
	@Override
	public boolean setBlockState(BlockPos pos, IBlockState state, BlockChangeFlag flag) {
		throw new RuntimeException();
	}
	
	@Override
	public boolean forceSpawnEntity(org.spongepowered.api.entity.Entity entity) {
		throw new RuntimeException();
	}
	
	@Override
	public void onSpongeEntityAdded(Entity entity) {
		throw new RuntimeException();
	}
	
	@Override
	public void onSpongeEntityRemoved(Entity entity) {
		throw new RuntimeException();
	}
	
	@Override
	public void addEntityRotationUpdate(Entity entity, com.flowpowered.math.vector.Vector3d rotation) {
		throw new RuntimeException();
	}
	
	@Override
	public SpongeBlockSnapshot createSpongeBlockSnapshot(IBlockState state, IBlockState extended, BlockPos pos, int updateFlag) {
		throw new RuntimeException();
	}
	
	@Override
	public SpongeWorldGenerator createWorldGenerator(DataContainer settings) {
		throw new RuntimeException();
	}
	
	@Override
	public SpongeWorldGenerator createWorldGenerator(String settings) {
		throw new RuntimeException();
	}
	
	@Override
	public SpongeChunkGenerator createChunkGenerator(SpongeWorldGenerator newGenerator) {
		throw new RuntimeException();
	}
	
	@Override
	public boolean isProcessingExplosion() {
		throw new RuntimeException();
	}
	
	@Override
	public boolean isMinecraftChunkLoaded(int x, int z, boolean allowEmpty) {
		return isChunkLoaded(x, z, allowEmpty);
	}
	
	@Override
	public WorldTimingsHandler getTimingsHandler() {
		throw new RuntimeException();
	}
	
	@Override
	public int getChunkGCTickInterval() {
		throw new RuntimeException();
	}
	
	@Override
	public long getChunkUnloadDelay() {
		throw new RuntimeException();
	}
	
	@Override
	public void triggerInternalExplosion(Explosion explosion) {
		throw new RuntimeException();
	}
	
	@Override
	public void playCustomSound(EntityPlayer player, double x, double y, double z, String soundIn, SoundCategory category, float volume, float pitch) {
		throw new RuntimeException();
	}
	
	@Override
	public void doChunkGC() {
		throw new RuntimeException();
	}
	
	@Override
	public boolean isLightLevel(Chunk chunk, BlockPos pos, int level) {
		throw new RuntimeException();
	}
	
	@Override
	public boolean updateLightAsync(EnumSkyBlock lightType, BlockPos pos, Chunk chunk) {
		throw new RuntimeException();
	}
	
	@Override
	public boolean checkLightAsync(EnumSkyBlock lightType, BlockPos pos, Chunk chunk, List<Chunk> neighbors) {
		throw new RuntimeException();
	}
	
	@Override
	public ExecutorService getLightingExecutor() {
		throw new RuntimeException();
	}
	
	@Override
	public int getRawBlockLight(BlockPos pos, EnumSkyBlock lightType) {
		throw new RuntimeException();
	}
	
	@Override
	public boolean isFake() {
		return true;
	}
	
}
