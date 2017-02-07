package com.creativemd.creativecore.common.world;

import org.spongepowered.common.interfaces.world.IMixinWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.Optional.Interface;


@Interface(modid = "sponge", iface = "org.spongepowered.common.interfaces.world.IMixinWorld")
public class WorldFake extends World implements IMixinWorld {
	
	private final World parentWorld;

	public WorldFake(World world) {
		super(new SaveHandlerFake(world.getWorldInfo()), world.getWorldInfo(), world.provider, new Profiler(), world.isRemote);
		chunkProvider = createChunkProvider();
		parentWorld = world;
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
	public long getWeatherStartTime() {
		return ((IMixinWorld) parentWorld).getWeatherStartTime();
	}

	@Override
	public void setWeatherStartTime(long weatherStartTime) {
		((IMixinWorld) parentWorld).setWeatherStartTime(weatherStartTime);;
	}

	@Override
	public EntityPlayer getClosestPlayerToEntityWhoAffectsSpawning(Entity entity, double d1tance) {
		return ((IMixinWorld) parentWorld).getClosestPlayerToEntityWhoAffectsSpawning(entity, d1tance);
	}

	@Override
	public EntityPlayer getClosestPlayerWhoAffectsSpawning(double x, double y, double z, double distance) {
		return ((IMixinWorld) parentWorld).getClosestPlayerWhoAffectsSpawning(x, y, z, distance);
	}

}
