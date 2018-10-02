package com.creativemd.creativecore.common.world;

import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;

import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldFake extends World implements IFakeWorld {
	
	public Entity parent;
	public final World parentWorld;
	public IVecOrigin origin;
	
	@SideOnly(Side.CLIENT)
	public boolean shouldRender;
	
	public static WorldFake createFakeWorld(World world) {
		if (world instanceof WorldServer)
			return new WorldFakeServer((WorldServer) world);
		return new WorldFake(world);
	}
	
	protected WorldFake(World world) {
		super(new SaveHandlerFake(world.getWorldInfo()), world.getWorldInfo(), world.provider, new Profiler(), world.isRemote);
		this.chunkProvider = createChunkProvider();
		this.parentWorld = world;
	}
	
	@Override
	public MinecraftServer getMinecraftServer() {
		return parentWorld.getMinecraftServer();
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return new ChunkProviderFake(this, this.saveHandler.getChunkLoader(provider), provider.createChunkGenerator());
	}
	
	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		return ((ChunkProviderFake) getChunkProvider()).chunkExists(x, z);
	}
	
	@Override
	public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
	}
	
	@Override
	public void spawnAlwaysVisibleParticle(int p_190523_1_, double p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_, double p_190523_10_, double p_190523_12_, int... p_190523_14_) {
		for (int i = 0; i < this.eventListeners.size(); ++i) {
			Vector3d pos = new Vector3d(p_190523_2_, p_190523_4_, p_190523_6_);
			origin.transformPointToWorld(pos);
			((IWorldEventListener) this.eventListeners.get(i)).spawnParticle(p_190523_1_, false, true, pos.x, pos.y, pos.z, p_190523_8_, p_190523_10_, p_190523_12_, p_190523_14_);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() || ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
	}
	
	private void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		for (int i = 0; i < this.eventListeners.size(); ++i) {
			Vector3d pos = new Vector3d(xCoord, yCoord, zCoord);
			origin.transformPointToWorld(pos);
			((IWorldEventListener) this.eventListeners.get(i)).spawnParticle(particleID, ignoreRange, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed, parameters);
		}
	}
	
	@Override
	public IVecOrigin getOrigin() {
		return origin;
	}
	
	@Override
	public void setOrigin(IVecOrigin origin) {
		this.origin = origin;
	}
	
}
