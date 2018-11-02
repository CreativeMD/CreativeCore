package com.creativemd.creativecore.common.world;

import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public abstract class CreativeWorld extends World implements IOrientatedWorld {
	
	public Entity parent;
	
	protected CreativeWorld(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}
}
