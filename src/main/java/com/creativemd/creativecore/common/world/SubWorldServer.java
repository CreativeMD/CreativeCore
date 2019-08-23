package com.creativemd.creativecore.common.world;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;

public class SubWorldServer extends SubWorld {
	
	protected SubWorldServer(WorldServer world) {
		super(world);
		this.chunkProvider = createChunkProvider();
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return new ChunkProviderFakeServer(this, this.saveHandler.getChunkLoader(provider), new ChunkGeneratorFake(this));
	}
	
}
