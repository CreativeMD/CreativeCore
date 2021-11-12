package team.creative.creativecore.common.level.listener;

import net.minecraft.core.BlockPos;
import team.creative.creativecore.common.level.CreativeLevel;
import team.creative.creativecore.common.level.system.BlockUpdateLevelSystem;
import team.creative.creativecore.common.util.math.base.Facing;

public interface LevelBoundsListener {
    
    public void rescan(CreativeLevel level, BlockUpdateLevelSystem system, Facing facing, Iterable<BlockPos> possible, int boundary);
    
    public void rescan(CreativeLevel level, BlockUpdateLevelSystem system, Iterable<BlockPos> possible);
    
}
