package com.creativemd.creativecore.common.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkProviderFakeServer extends ChunkProviderFake {
	
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
}
