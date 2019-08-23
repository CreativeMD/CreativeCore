package com.creativemd.creativecore.common.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

public class FakeWorldServer extends FakeWorld {
	
	protected FakeWorldServer(MinecraftServer server, WorldInfo info, WorldProvider provider, boolean isRemote) {
		super(server, info, provider, isRemote);
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return new ChunkProviderFakeServer(this, this.saveHandler.getChunkLoader(provider), new ChunkGeneratorFake(this));
	}
	
}
