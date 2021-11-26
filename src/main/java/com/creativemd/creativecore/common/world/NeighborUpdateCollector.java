package com.creativemd.creativecore.common.world;

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NeighborUpdateCollector {
    
    protected final World world;
    private final HashSet<BlockPos> blocksToUpdate;
    
    public NeighborUpdateCollector(World world, Collection<BlockPos> positions) {
        this.world = world;
        blocksToUpdate = new HashSet<>(positions);
    }
    
    public NeighborUpdateCollector(World world) {
        this.world = world;
        blocksToUpdate = new HashSet<>();
    }
    
    public void add(BlockPos pos) {
        blocksToUpdate.add(pos);
    }
    
    public void add(TileEntity te) {
        blocksToUpdate.add(te.getPos());
    }
    
    public void add(Collection<BlockPos> positions) {
        blocksToUpdate.addAll(positions);
    }
    
    protected void processPosition(BlockPos pos, HashSet<BlockPos> notifiedBlocks) {
        IBlockState origin = world.getBlockState(pos);
        
        for (int i = 0; i < 6; i++) {
            BlockPos neighbour = pos.offset(EnumFacing.VALUES[i]);
            if (!notifiedBlocks.contains(neighbour) && !blocksToUpdate.contains(neighbour)) {
                world.getBlockState(neighbour).neighborChanged(world, neighbour, origin.getBlock(), pos);
                notifiedBlocks.add(neighbour);
            }
        }
    }
    
    public void process() {
        HashSet<BlockPos> notifiedBlocks = new HashSet<>();
        for (BlockPos pos : blocksToUpdate)
            processPosition(pos, notifiedBlocks);
        blocksToUpdate.clear();
    }
    
}
