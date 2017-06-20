package com.creativemd.creativecore.common.world;

import java.io.File;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class SaveHandlerFake implements ISaveHandler {
	
	public WorldInfo info;
	
	public SaveHandlerFake(WorldInfo info) {
		this.info = info;
	}

	@Override
	public WorldInfo loadWorldInfo() {
		return info;
	}

	@Override
	public void checkSessionLock() throws MinecraftException {
		
	}

	@Override
	public IChunkLoader getChunkLoader(WorldProvider provider) {
		return new IChunkLoader() {
			
			@Override
			public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {
				
			}
			
			@Override
			public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {
				
			}
			
			@Override
			public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
				return new Chunk(worldIn, x, z);
			}
			
			@Override
			public void chunkTick() {
				
			}

			@Override
			public boolean isChunkGeneratedAt(int x, int z) {
				return true;
			}

			@Override
			public void flush() {
				
			}
		};
	}

	@Override
	public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
		
	}

	@Override
	public void saveWorldInfo(WorldInfo worldInformation) {
		
	}

	@Override
	public IPlayerFileData getPlayerNBTManager() {
		return new IPlayerFileData() {
			
			@Override
			public void writePlayerData(EntityPlayer player) {
				
			}
			
			@Override
			public NBTTagCompound readPlayerData(EntityPlayer player) {
				return new NBTTagCompound();
			}
			
			@Override
			public String[] getAvailablePlayerDat() {
				return new String[0];
			}
		};
	}

	@Override
	public void flush() {
		
	}

	@Override
	public File getWorldDirectory() {
		return null;
	}

	@Override
	public File getMapFileFromName(String mapName) {
		return null;
	}

	@Override
	public TemplateManager getStructureTemplateManager() {
		return null;
	}

}
