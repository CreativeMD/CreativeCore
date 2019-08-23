package com.creativemd.creativecore.common.world;

import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;
import com.creativemd.creativecore.common.utils.math.vec.VecOrigin;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class FakeWorld extends CreativeWorld {
	
	public MinecraftServer server;
	public IVecOrigin origin;
	
	@SideOnly(Side.CLIENT)
	public boolean shouldRender;
	
	public static FakeWorld createFakeWorld(String name, boolean isRemote) {
		WorldInfo info = new WorldInfo(new WorldSettings(-1, GameType.NOT_SET, false, false, WorldType.CUSTOMIZED), name);
		SaveHandlerFake saveHandler = new SaveHandlerFake(info);
		WorldProvider provider = new WorldProvider() {
			
			@Override
			public DimensionType getDimensionType() {
				return DimensionType.OVERWORLD;
			}
			
			@Override
			public boolean canDropChunk(int x, int z) {
				return !this.world.isSpawnChunk(x, z) || !this.world.provider.getDimensionType().shouldLoadSpawn();
			}
			
		};
		if (!isRemote)
			return new FakeWorldServer(FMLServerHandler.instance().getServer(), info, provider, isRemote);
		return new FakeWorld(null, info, provider, isRemote);
	}
	
	protected FakeWorld(MinecraftServer server, WorldInfo info, WorldProvider provider, boolean isRemote) {
		super(new SaveHandlerFake(info), info, provider, new Profiler(), isRemote);
		provider.setWorld(this);
		this.chunkProvider = createChunkProvider();
		this.server = server;
	}
	
	@Override
	public MinecraftServer getMinecraftServer() {
		return server;
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return new ChunkProviderFake(this, this.saveHandler.getChunkLoader(provider), new ChunkGeneratorFake(this));
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
		Vector3d pos = new Vector3d(p_190523_2_, p_190523_4_, p_190523_6_);
		origin.transformPointToWorld(pos);
		for (int i = 0; i < this.eventListeners.size(); ++i) {
			this.eventListeners.get(i).spawnParticle(p_190523_1_, false, true, pos.x, pos.y, pos.z, p_190523_8_, p_190523_10_, p_190523_12_, p_190523_14_);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		Vector3d pos = new Vector3d(xCoord, yCoord, zCoord);
		origin.transformPointToWorld(pos);
		this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() || ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
	}
	
	private void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		Vector3d pos = new Vector3d(xCoord, yCoord, zCoord);
		origin.transformPointToWorld(pos);
		for (int i = 0; i < this.eventListeners.size(); ++i) {
			
			this.eventListeners.get(i).spawnParticle(particleID, ignoreRange, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed, parameters);
		}
	}
	
	@Override
	public IVecOrigin getOrigin() {
		return origin;
	}
	
	@Override
	public void setOrigin(Vector3d vec) {
		this.origin = new VecOrigin(vec);
	}
	
	@Override
	public boolean hasParent() {
		return false;
	}
	
	@Override
	public World getParent() {
		return null;
	}
	
}
