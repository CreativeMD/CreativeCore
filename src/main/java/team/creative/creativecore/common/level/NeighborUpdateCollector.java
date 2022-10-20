package team.creative.creativecore.common.level;

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NeighborUpdateCollector {
    
    protected final Level level;
    private final HashSet<BlockPos> blocksToUpdate;
    
    public NeighborUpdateCollector(Level level, Collection<BlockPos> positions) {
        this.level = level;
        blocksToUpdate = new HashSet<>(positions);
    }
    
    public NeighborUpdateCollector(Level level) {
        this.level = level;
        blocksToUpdate = new HashSet<>();
    }
    
    public void add(BlockPos pos) {
        blocksToUpdate.add(pos);
    }
    
    public void add(BlockEntity be) {
        blocksToUpdate.add(be.getBlockPos());
    }
    
    public void add(Collection<BlockPos> positions) {
        blocksToUpdate.addAll(positions);
    }
    
    protected void processPosition(BlockPos pos, HashSet<BlockPos> notifiedBlocks) {
        BlockState origin = level.getBlockState(pos);
        
        for (int i = 0; i < 6; i++) {
            BlockPos neighbour = pos.relative(Direction.values()[i]);
            if (!notifiedBlocks.contains(neighbour) && !blocksToUpdate.contains(neighbour)) {
                level.getBlockState(neighbour).neighborChanged(level, neighbour, origin.getBlock(), pos, false);
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