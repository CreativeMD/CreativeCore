package team.creative.creativecore.common.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ISubLevel extends IOrientatedLevel {
    
    public Level getParent();
    
    public Level getRealLevel();
    
    public default BlockPos transformToRealWorld(BlockPos pos) {
        return getOrigin().transformPointToWorld(pos);
    }
    
}
