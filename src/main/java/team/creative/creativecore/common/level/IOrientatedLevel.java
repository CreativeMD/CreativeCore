package team.creative.creativecore.common.level;

import net.minecraft.world.level.Level;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public interface IOrientatedLevel {
    
    public boolean hasParent();
    
    public Level getParent();
    
    public Level getRealLevel();
    
    public IVecOrigin getOrigin();
    
    public void setOrigin(Vec3d center);
}
