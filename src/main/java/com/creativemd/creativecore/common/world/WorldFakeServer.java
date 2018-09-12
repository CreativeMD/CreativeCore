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

import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;

import co.aikar.timings.WorldTimingsHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(modid = "sponge", iface = "org.spongepowered.common.interfaces.world.IMixinWorldServer")
public class WorldFakeServer extends WorldFake implements IMixinWorldServer {

	public final World parentWorld;
	public IVecOrigin origin;

	protected WorldFakeServer(WorldServer world) {
		/*
		 * super(world.getMinecraftServer(), new SaveHandlerFake(world.getWorldInfo()),
		 * world.getWorldInfo(), DimensionType.OVERWORLD.getId(), new Profiler());
		 */
		super(world);
		chunkProvider = createChunkProvider();
		parentWorld = world;
	}

	/*
	 * @Override public World init() { this.initCapabilities(); return this; }
	 * 
	 * @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
	 * return ((ChunkProviderFakeServer) getChunkProvider()).chunkExists(x, z); }
	 * 
	 * @Override public void spawnParticle(EnumParticleTypes particleType, double
	 * xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double
	 * zSpeed, int... parameters) { this.spawnParticle(particleType.getParticleID(),
	 * particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed,
	 * zSpeed, parameters); }
	 * 
	 * @Override public void spawnAlwaysVisibleParticle(int p_190523_1_, double
	 * p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_,
	 * double p_190523_10_, double p_190523_12_, int... p_190523_14_) { for (int i =
	 * 0; i < this.eventListeners.size(); ++i) { Vector3d pos = new
	 * Vector3d(p_190523_2_, p_190523_4_, p_190523_6_);
	 * origin.transformPointToWorld(pos);
	 * ((IWorldEventListener)this.eventListeners.get(i)).spawnParticle(p_190523_1_,
	 * false, true, pos.x, pos.y, pos.z, p_190523_8_, p_190523_10_, p_190523_12_,
	 * p_190523_14_); } }
	 * 
	 * private void spawnParticle(int particleID, boolean ignoreRange, double
	 * xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double
	 * zSpeed, int... parameters) { for (int i = 0; i < this.eventListeners.size();
	 * ++i) { Vector3d pos = new Vector3d(xCoord, yCoord, zCoord);
	 * origin.transformPointToWorld(pos);
	 * ((IWorldEventListener)this.eventListeners.get(i)).spawnParticle(particleID,
	 * ignoreRange, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed, parameters); } }
	 */

	@Override
	protected IChunkProvider createChunkProvider() {
		return new ChunkProviderFakeServer((WorldFakeServer) this, this.saveHandler.getChunkLoader(provider), provider.createChunkGenerator());
	}

	@Override
	public long getWeatherStartTime() {
		return ((IMixinWorldServer) parentWorld).getWeatherStartTime();
	}

	@Override
	public void setWeatherStartTime(long weatherStartTime) {
		((IMixinWorldServer) parentWorld).setWeatherStartTime(weatherStartTime);
	}

	@Override
	public void setCallingWorldEvent(boolean flag) {
		((IMixinWorldServer) parentWorld).setCallingWorldEvent(flag);
	}

	@Override
	public EntityPlayer getClosestPlayerToEntityWhoAffectsSpawning(Entity entity, double d1tance) {
		return ((IMixinWorldServer) parentWorld).getClosestPlayerToEntityWhoAffectsSpawning(entity, d1tance);
	}

	@Override
	public EntityPlayer getClosestPlayerWhoAffectsSpawning(double x, double y, double z, double distance) {
		return ((IMixinWorldServer) parentWorld).getClosestPlayerWhoAffectsSpawning(x, y, z, distance);
	}

	@Override
	public SpongeConfig<?> getActiveConfig() {
		return ((IMixinWorldServer) parentWorld).getActiveConfig();
	}

	@Override
	public SpongeConfig<WorldConfig> getWorldConfig() {
		return ((IMixinWorldServer) parentWorld).getWorldConfig();
	}

	@Override
	public void setActiveConfig(SpongeConfig<?> config) {
		((IMixinWorldServer) parentWorld).setActiveConfig(config);
	}

	@Override
	public Integer getDimensionId() {
		return ((IMixinWorldServer) parentWorld).getDimensionId();
	}

	@Override
	public void updateWorldGenerator() {
		((IMixinWorldServer) parentWorld).updateWorldGenerator();
	}

	@Override
	public void updateRotation(Entity entityIn) {
		((IMixinWorldServer) parentWorld).updateRotation(entityIn);
	}

	@Override
	public void spongeNotifyNeighborsPostBlockChange(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		((IMixinWorldServer) parentWorld).spongeNotifyNeighborsPostBlockChange(pos, oldState, newState, flags);
	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState state, BlockChangeFlag flag) {
		return ((IMixinWorldServer) parentWorld).setBlockState(pos, state, flag);
	}

	@Override
	public boolean forceSpawnEntity(org.spongepowered.api.entity.Entity entity) {
		return ((IMixinWorldServer) parentWorld).forceSpawnEntity(entity);
	}

	@Override
	public void onSpongeEntityAdded(Entity entity) {
		((IMixinWorldServer) parentWorld).onSpongeEntityAdded(entity);
	}

	@Override
	public void onSpongeEntityRemoved(Entity entity) {
		((IMixinWorldServer) parentWorld).onSpongeEntityRemoved(entity);
	}

	@Override
	public void addEntityRotationUpdate(Entity entity, com.flowpowered.math.vector.Vector3d rotation) {
		((IMixinWorldServer) parentWorld).addEntityRotationUpdate(entity, rotation);
	}

	@Override
	public SpongeBlockSnapshot createSpongeBlockSnapshot(IBlockState state, IBlockState extended, BlockPos pos, int updateFlag) {
		return ((IMixinWorldServer) parentWorld).createSpongeBlockSnapshot(state, extended, pos, updateFlag);
	}

	@Override
	public SpongeWorldGenerator createWorldGenerator(DataContainer settings) {
		return ((IMixinWorldServer) parentWorld).createWorldGenerator(settings);
	}

	@Override
	public SpongeWorldGenerator createWorldGenerator(String settings) {
		return ((IMixinWorldServer) parentWorld).createWorldGenerator(settings);
	}

	@Override
	public SpongeChunkGenerator createChunkGenerator(SpongeWorldGenerator newGenerator) {
		return ((IMixinWorldServer) parentWorld).createChunkGenerator(newGenerator);
	}

	@Override
	public boolean isProcessingExplosion() {
		return ((IMixinWorldServer) parentWorld).isProcessingExplosion();
	}

	@Override
	public boolean isMinecraftChunkLoaded(int x, int z, boolean allowEmpty) {
		return isChunkLoaded(x, z, allowEmpty);
	}

	@Override
	public WorldTimingsHandler getTimingsHandler() {
		return ((IMixinWorldServer) parentWorld).getTimingsHandler();
	}

	@Override
	public int getChunkGCTickInterval() {
		return ((IMixinWorldServer) parentWorld).getChunkGCTickInterval();
	}

	@Override
	public long getChunkUnloadDelay() {
		return ((IMixinWorldServer) parentWorld).getChunkUnloadDelay();
	}

	@Override
	public void triggerInternalExplosion(Explosion explosion) {
		((IMixinWorldServer) parentWorld).triggerInternalExplosion(explosion);
	}

	@Override
	public void playCustomSound(EntityPlayer player, double x, double y, double z, String soundIn, SoundCategory category, float volume, float pitch) {
		((IMixinWorldServer) parentWorld).playCustomSound(player, x, y, z, soundIn, category, volume, pitch);
	}

	@Override
	public void doChunkGC() {
		((IMixinWorldServer) parentWorld).doChunkGC();
	}

	@Override
	public boolean isLightLevel(Chunk chunk, BlockPos pos, int level) {
		return ((IMixinWorldServer) parentWorld).isLightLevel(chunk, pos, level);
	}

	@Override
	public boolean updateLightAsync(EnumSkyBlock lightType, BlockPos pos, Chunk chunk) {
		return ((IMixinWorldServer) parentWorld).updateLightAsync(lightType, pos, chunk);
	}

	@Override
	public boolean checkLightAsync(EnumSkyBlock lightType, BlockPos pos, Chunk chunk, List<Chunk> neighbors) {
		return ((IMixinWorldServer) parentWorld).checkLightAsync(lightType, pos, chunk, neighbors);
	}

	@Override
	public ExecutorService getLightingExecutor() {
		return ((IMixinWorldServer) parentWorld).getLightingExecutor();
	}

	@Override
	public int getRawBlockLight(BlockPos pos, EnumSkyBlock lightType) {
		return ((IMixinWorldServer) parentWorld).getRawBlockLight(pos, lightType);
	}

	@Override
	public boolean isFake() {
		return true;
	}

}
