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
		try {
			if (Class.forName("org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer") != null)
				return new ChunkProviderFakeServerSponge(this, this.saveHandler.getChunkLoader(provider), new ChunkGeneratorFake(this));
		} catch (ClassNotFoundException e) {
			
		}
		return new ChunkProviderFakeServer(this, this.saveHandler.getChunkLoader(provider), new ChunkGeneratorFake(this));
	}
	
}
