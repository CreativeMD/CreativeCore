package team.creative.creativecore.common.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.extensions.IForgeLevel;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public interface IOrientatedLevel extends LevelAccessor, IForgeLevel {
    
    public IVecOrigin getOrigin();
    
    public void setOrigin(Vec3d center);
    
    public Entity getHolder();
    
    public void setHolder(Entity entity);
    
    public void unload();
}
