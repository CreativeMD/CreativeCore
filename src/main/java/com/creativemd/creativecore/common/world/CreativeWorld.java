package com.creativemd.creativecore.common.world;

import com.creativemd.creativecore.client.rendering.IRenderChunkSupplier;

import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CreativeWorld extends World implements IOrientatedWorld {
	
	public Entity parent;
	@SideOnly(Side.CLIENT)
	public IRenderChunkSupplier renderChunkSupplier;
	
	protected CreativeWorld(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}
}
