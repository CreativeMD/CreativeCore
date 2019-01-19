package com.creativemd.creativecore.common.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(modid = "sponge", iface = "org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer")
public class ChunkProviderFakeServer extends ChunkProviderFake implements IMixinChunkProviderServer {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public ChunkProviderFakeServer(World worldObjIn, IChunkLoader chunkLoaderIn, IChunkGenerator chunkGeneratorIn) {
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
		long i = ChunkPos.asLong(x, z);
		Chunk chunk = (Chunk) this.id2ChunkMap.get(i);
		return chunk;
	}
	
	@Override
	public long getChunkUnloadDelay() {
		return 15000;
	}
	
	@Override
	public boolean markLoaded() {
		return false;
	}
	
	@Override
	public WorldServer getWorld() {
		throw new RuntimeException("This is not a WorldServer provider!");
	}
	
}
