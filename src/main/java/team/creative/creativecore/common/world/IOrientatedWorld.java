package team.creative.creativecore.common.world;

import net.minecraft.world.World;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public interface IOrientatedWorld {
    
    public boolean hasParent();
    
    public World getParent();
    
    public World getRealWorld();
    
    public IVecOrigin getOrigin();
    
    public void setOrigin(Vec3d center);
}
