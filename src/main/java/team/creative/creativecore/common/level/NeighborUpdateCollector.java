package team.creative.creativecore.common.level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import team.creative.creativecore.common.util.type.map.HashMapList;

public class NeighborUpdateCollector {
    
    private final HashMapList<Level, BlockPos> blocksToUpdate = new HashMapList<>();
    
    public NeighborUpdateCollector(Level level, Collection<BlockPos> positions) {
        blocksToUpdate.add(level, positions);
    }
    
    public NeighborUpdateCollector() {}
    
    public void add(Level level, BlockPos pos) {
        blocksToUpdate.add(level, pos);
    }
    
    public void add(BlockEntity be) {
        blocksToUpdate.add(be.getLevel(), be.getBlockPos());
    }
    
    public void add(Level level, Collection<BlockPos> positions) {
        blocksToUpdate.add(level, positions);
    }
    
    protected void processPosition(Level level, BlockPos pos, HashSet<BlockPos> notifiedBlocks) {
        BlockState origin = level.getBlockState(pos);
        
        for (int i = 0; i < 6; i++) {
            BlockPos neighbour = pos.relative(Direction.values()[i]);
            if (!notifiedBlocks.contains(neighbour) && !blocksToUpdate.contains(neighbour)) {
                level.getBlockState(neighbour).handleNeighborChanged(level, neighbour, origin.getBlock(), pos, false);
                notifiedBlocks.add(neighbour);
            }
        }
    }
    
    public void process(Level level) {
        HashSet<BlockPos> notifiedBlocks = new HashSet<>();
        ArrayList<BlockPos> positions = blocksToUpdate.removeKey(level);
        for (BlockPos pos : positions)
            processPosition(level, pos, notifiedBlocks);
    }
    
    public void process() {
        HashSet<BlockPos> notifiedBlocks = new HashSet<>();
        for (Entry<Level, ArrayList<BlockPos>> entry : blocksToUpdate.entrySet()) {
            for (BlockPos pos : entry.getValue())
                processPosition(entry.getKey(), pos, notifiedBlocks);
            notifiedBlocks.clear();
        }
        blocksToUpdate.clear();
    }
    
}