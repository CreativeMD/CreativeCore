package team.creative.creativecore.common.level;

import net.minecraft.world.entity.Entity;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public interface IOrientatedLevel {
    
    public IVecOrigin getOrigin();
    
    public void setOrigin(Vec3d center);
    
    public Entity getHolder();
    
    public void setHolder(Entity entity);
}
