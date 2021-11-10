package com.creativemd.creativecore.common.world;

import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface IOrientatedWorld {
    
    public boolean hasParent();
    
    public World getParent();
    
    public World getRealWorld();
    
    public IVecOrigin getOrigin();
    
    public void setOrigin(Vector3d center);
    
    public Entity getParentEntity();
    
    public default Entity getTopEntity() {
        World world = getParent();
        if (world instanceof IOrientatedWorld)
            return ((IOrientatedWorld) world).getTopEntity();
        return getParentEntity();
    }
}
