package com.creativemd.creativecore.common.world;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer;

import com.flowpowered.math.vector.Vector3i;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(modid = "spongeforge", iface = "org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer")
public class ChunkProviderFakeServer extends ChunkProviderFake implements IMixinChunkProviderServer {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public ChunkProviderFakeServer(CreativeWorld worldObjIn, IChunkLoader chunkLoaderIn, IChunkGenerator chunkGeneratorIn) {
		super(worldObjIn, chunkLoaderIn, chunkGeneratorIn);
	}
	
	@Override
	public boolean tick() {
		return false;
	}
	
	@Override
	public boolean canSave() {
		return false;
	}
	
	@Override
	public boolean getForceChunkRequests() {
		return false;
	}
	
	@Override
	public void setMaxChunkUnloads(int maxUnloads) {
		
	}
	
	@Override
	public void setDenyChunkRequests(boolean flag) {
		
	}
	
	@Override
	public void setForceChunkRequests(boolean flag) {
		
	}
	
	@Override
	public Chunk getLoadedChunkWithoutMarkingActive(int x, int z) {
		return getLoadedChunk(x, z);
	}
	
	@Override
	public long getChunkUnloadDelay() {
		return 0;
	}
	
	@Override
	public WorldServer getWorld() {
		return null;
	}
	
	@Override
	public CompletableFuture<Boolean> doesChunkExistSync(Vector3i chunkCoords) {
		return null;
	}
	
	@Override
	public void unloadChunkAndSave(Chunk chunk) {
		
	}
}
