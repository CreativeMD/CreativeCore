package com.creativemd.creativecore.common.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.common.bridge.world.chunk.ChunkProviderBridge;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(modid = "spongeforge", iface = "org.spongepowered.common.bridge.world.chunk.ChunkProviderBridge")
public class ChunkProviderFakeServer extends ChunkProviderFake implements ChunkProviderBridge {
	
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
	public void bridge$setMaxChunkUnloads(int maxUnloads) {
		
	}
	
	@Override
	public Chunk bridge$getLoadedChunkWithoutMarkingActive(int x, int z) {
		return getLoadedChunk(x, z);
	}
}
