package team.creative.creativecore.common.util.math.collision;

import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.box.ABB;
import team.creative.creativecore.common.util.math.box.BoxFace;
import team.creative.creativecore.common.util.math.box.BoxUtils;
import team.creative.creativecore.common.util.math.utils.BooleanUtils;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class PlaneCache {
    
    public final CollidingPlane[] planes;
    public final Vec3d center;
    public final double radiusSquared;
    public final AABB bb;
    
    public PlaneCache(AABB box, CollisionCoordinator coordinator) {
        this.bb = box;
        double halfSizeX = (box.maxX - box.minX) * 0.5D;
        double halfSizeY = (box.maxY - box.minY) * 0.5D;
        double halfSizeZ = (box.maxZ - box.minZ) * 0.5D;
        this.radiusSquared = halfSizeX * halfSizeX + halfSizeY * halfSizeY + halfSizeZ * halfSizeZ;
        this.center = new Vec3d(box.minX + halfSizeX, box.minY + halfSizeY, box.minZ + halfSizeZ);
        this.planes = calculatePlanes(coordinator);
    }
    
    protected CollidingPlane[] calculatePlanes(CollisionCoordinator coordinator) {
        Vec3d[] corners = BoxUtils.getRotatedCorners(bb, coordinator.original());
        
        boolean east = coordinator.offX > 0;
        boolean west = coordinator.offY < 0;
        boolean up = coordinator.offY > 0;
        boolean down = coordinator.offY < 0;
        boolean south = coordinator.offZ > 0;
        boolean north = coordinator.offZ < 0;
        
        if (coordinator.hasRotY || coordinator.hasRotZ)
            east = west = true;
        
        if (coordinator.hasRotX || coordinator.hasRotZ)
            up = down = true;
        
        if (coordinator.hasRotX || coordinator.hasRotY)
            south = north = true;
        
        CollidingPlane[] planes = new CollidingPlane[BooleanUtils.countTrue(east, west, up, down, south, north)];
        int index = 0;
        
        if (east) {
            planes[index] = new CollidingPlane(bb, Facing.EAST, this, corners, BoxFace.get(Facing.EAST).corners);
            index++;
        }
        if (west) {
            planes[index] = new CollidingPlane(bb, Facing.WEST, this, corners, BoxFace.get(Facing.WEST).corners);
            index++;
        }
        if (up) {
            planes[index] = new CollidingPlane(bb, Facing.UP, this, corners, BoxFace.get(Facing.UP).corners);
            index++;
        }
        if (down) {
            planes[index] = new CollidingPlane(bb, Facing.DOWN, this, corners, BoxFace.get(Facing.DOWN).corners);
            index++;
        }
        if (south) {
            planes[index] = new CollidingPlane(bb, Facing.SOUTH, this, corners, BoxFace.get(Facing.SOUTH).corners);
            index++;
        }
        if (north) {
            planes[index] = new CollidingPlane(bb, Facing.NORTH, this, corners, BoxFace.get(Facing.NORTH).corners);
            index++;
        }
        
        return planes;
    }
    
    public double getPushOutScale(double minScale, ABB fakeBox, Vec3d pushVec) {
        double scale = Double.MAX_VALUE;
        
        boolean pushX = pushVec.x != 0;
        boolean pushY = pushVec.y != 0;
        boolean pushZ = pushVec.z != 0;
        
        if (pushX)
            if (pushVec.x > 0)
                scale = Math.min(scale, Math.abs((bb.maxX - fakeBox.minX) / pushVec.x));
            else
                scale = Math.min(scale, Math.abs((bb.minX - fakeBox.maxX) / pushVec.x));
            
        if (pushY)
            if (pushVec.y > 0)
                scale = Math.min(scale, Math.abs((bb.maxY - fakeBox.minY) / pushVec.y));
            else
                scale = Math.min(scale, Math.abs((bb.minY - fakeBox.maxY) / pushVec.y));
            
        if (pushZ)
            if (pushVec.z > 0)
                scale = Math.min(scale, Math.abs((bb.maxZ - fakeBox.minZ) / pushVec.z));
            else
                scale = Math.min(scale, Math.abs((bb.minZ - fakeBox.maxZ) / pushVec.z));
            
        if (scale <= minScale)
            return minScale;
        
        return scale;
        
    }
    
}