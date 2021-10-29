package team.creative.creativecore.common.util.math.box;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class CreativeAABB extends AABB {
    
    public CreativeAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
    }
    
    public CreativeAABB(BlockPos pos) {
        super(pos);
    }
    
    public CreativeAABB(BlockPos pos1, BlockPos pos2) {
        super(pos1, pos2);
    }
    
    public boolean contains(Vec3d vec) {
        if (vec.x > this.minX && vec.x < this.maxX) {
            if (vec.y > this.minY && vec.y < this.maxY) {
                return vec.z > this.minZ && vec.z < this.maxZ;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    protected double get(Facing facing) {
        switch (facing) {
        case EAST:
            return maxX;
        case WEST:
            return minX;
        case UP:
            return maxY;
        case DOWN:
            return minY;
        case SOUTH:
            return maxZ;
        case NORTH:
            return minZ;
        
        }
        return 0;
    }
    
    public Vec3d getCorner(BoxCorner corner) {
        return new Vec3d(getCornerX(corner), getCornerY(corner), getCornerZ(corner));
    }
    
    public double getCornerValue(BoxCorner corner, Axis axis) {
        return get(corner.getFacing(axis));
    }
    
    public double getCornerX(BoxCorner corner) {
        return get(corner.x);
    }
    
    public double getCornerY(BoxCorner corner) {
        return get(corner.y);
    }
    
    public double getCornerZ(BoxCorner corner) {
        return get(corner.z);
    }
    
    public Vec3d getSizeVec() {
        return new Vec3d(maxX - minX, maxY - minY, maxZ - minZ);
    }
    
    public double getVolume() {
        return (maxX - minX) * (maxY - minY) * (maxZ - minZ);
    }
    
    public double getIntersectionVolume(AABB other) {
        double d0 = Math.max(this.minX, other.minX);
        double d1 = Math.max(this.minY, other.minY);
        double d2 = Math.max(this.minZ, other.minZ);
        double d3 = Math.min(this.maxX, other.maxX);
        double d4 = Math.min(this.maxY, other.maxY);
        double d5 = Math.min(this.maxZ, other.maxZ);
        if (d0 < d3 && d1 < d4 && d2 < d5)
            return Math.abs((d3 - d0) * (d4 - d1) * (d5 - d2));
        return 0;
    }
    
    public double getSize(Axis axis) {
        switch (axis) {
        case X:
            return maxX - minX;
        case Y:
            return maxY - minY;
        case Z:
            return maxZ - minZ;
        }
        return 0;
    }
    
    public double getMin(Axis axis) {
        switch (axis) {
        case X:
            return minX;
        case Y:
            return minY;
        case Z:
            return minZ;
        }
        return 0;
    }
    
    public double getMax(Axis axis) {
        switch (axis) {
        case X:
            return maxX;
        case Y:
            return maxY;
        case Z:
            return maxZ;
        }
        return 0;
    }
    
    public static double get(AABB bb, Facing facing) {
        switch (facing) {
        case EAST:
            return bb.maxX;
        case WEST:
            return bb.minX;
        case UP:
            return bb.maxY;
        case DOWN:
            return bb.minY;
        case SOUTH:
            return bb.maxZ;
        case NORTH:
            return bb.minZ;
        }
        return 0;
    }
    
    public static double getMin(AABB bb, Axis axis) {
        switch (axis) {
        case X:
            return bb.minX;
        case Y:
            return bb.minY;
        case Z:
            return bb.minZ;
        default:
            return 0;
        }
    }
    
    public static double getMax(AABB bb, Axis axis) {
        switch (axis) {
        case X:
            return bb.maxX;
        case Y:
            return bb.maxY;
        case Z:
            return bb.maxZ;
        default:
            return 0;
        }
    }
    
    public static Vec3d getCorner(AABB bb, BoxCorner corner) {
        return new Vec3d(getCornerX(bb, corner), getCornerY(bb, corner), getCornerZ(bb, corner));
    }
    
    public static double getCornerValue(AABB bb, BoxCorner corner, Axis axis) {
        return get(bb, corner.getFacing(axis));
    }
    
    public static double getCornerX(AABB bb, BoxCorner corner) {
        return get(bb, corner.x);
    }
    
    public static double getCornerY(AABB bb, BoxCorner corner) {
        return get(bb, corner.y);
    }
    
    public static double getCornerZ(AABB bb, BoxCorner corner) {
        return get(bb, corner.z);
    }
}
