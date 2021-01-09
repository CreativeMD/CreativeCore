package com.creativemd.creativecore.common.world;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkGeneratorFake implements IChunkGenerator {
    
    public final World world;
    
    public ChunkGeneratorFake(World world) {
        this.world = world;
    }
    
    @Override
    public Chunk generateChunk(int x, int z) {
        return new Chunk(this.world, x, z);
    }
    
    @Override
    public void populate(int x, int z) {
        
    }
    
    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }
    
    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return Collections.emptyList();
    }
    
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return position;
    }
    
    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        
    }
    
    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
    
}
