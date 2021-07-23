package team.creative.creativecore.common.util.math.collision;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.creative.creativecore.common.util.math.collision.CollidingPlane.PlaneCache;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;

public class OrientatedVoxelShape extends SliceShape {
    
    public IVecOrigin origin;
    public PlaneCache cache;
    
    public void buildCache() {
        //this.cache = new PlaneCache(this);
    }
    
    public OrientatedVoxelShape(VoxelShape p_i47682_1_, Axis p_i47682_2_, int p_i47682_3_) {
        super(p_i47682_1_, p_i47682_2_, p_i47682_3_);
    }
    
}
