package team.creative.creativecore.common.util.math.collision;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.box.BoxCorner;
import team.creative.creativecore.common.util.math.box.BoxFace;
import team.creative.creativecore.common.util.math.box.BoxUtils;
import team.creative.creativecore.common.util.math.matrix.Matrix4;
import team.creative.creativecore.common.util.math.utils.BooleanUtils;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class CollidingPlane {
    
    public final AABB bb;
    public final Facing facing;
    public final PlaneCache cache;
    protected final Vec3d origin;
    protected final Vec3d normal;
    
    public CollidingPlane(AABB bb, Facing facing, PlaneCache cache, Vec3d[] corners, BoxCorner[] planeCorners) {
        this.bb = bb;
        this.facing = facing;
        this.cache = cache;
        this.origin = corners[planeCorners[0].ordinal()];
        Vec3d first = corners[planeCorners[1].ordinal()];
        Vec3d second = corners[planeCorners[2].ordinal()];
        
        this.normal = new Vec3d((first.y - origin.y) * (second.z - origin.z) - (first.z - origin.z) * (second.y - origin.y), (first.z - origin.z) * (second.x - origin.x) - (first.x - origin.x) * (second.z - origin.z), (first.x - origin.x) * (second.y - origin.y) - (first.y - origin.y) * (second.x - origin.x));
    }
    
    public Boolean isInFront(Vec3d vec) {
        double scalar = (vec.x - origin.x) * normal.x + (vec.y - origin.y) * normal.y + (vec.z - origin.z) * normal.z;
        if (scalar > 0)
            return true;
        else if (scalar < 0)
            return false;
        return null;
    }
    
    public static final int accuracySteps = 10;
    
    public Double binarySearch(@Nullable Double value, AABB toCheck, double checkRadiusSquared, Vec3d center, CollisionCoordinator coordinator) {
        if (coordinator.isSimple) {
            Double t = searchBetweenSimple(value, center, new Vec3d(center), new Vec3d(), 0, 1, coordinator, 0);
            if (t != null && intersects(toCheck, checkRadiusSquared, center, t, coordinator))
                return t;
            
            return null;
        } else if (coordinator.hasOneRotation && !coordinator.hasTranslation) {
            int halfRotations = coordinator.getNumberOfHalfRotations();
            double halfRotationSize = 1D / halfRotations;
            
            Vec3d temp = new Vec3d();
            Vec3d start = new Vec3d(center);
            
            Double t = searchBetweenSimple(value, center, start, temp, 0, halfRotationSize, coordinator, 0);
            if (t != null && intersects(toCheck, checkRadiusSquared, center, t, coordinator))
                return t;
            
            start.set(center);
            coordinator.transformInverted(start, halfRotationSize);
            t = searchBetweenSimple(value, center, new Vec3d(center), temp, halfRotationSize, halfRotationSize * 2, coordinator, 0);
            if (t != null && intersects(toCheck, checkRadiusSquared, center, t, coordinator))
                return t;
            
            return null;
        }
        
        // Advanced!!!!!!! At this point there is no way to figure out how the matrix
        // behaves, so just scan somewhere with the given resolution and hope to find
        // the earliest hit
        Vec3d start = new Vec3d(center);
        Vec3d temp = new Vec3d();
        int halfRotations = coordinator.getNumberOfHalfRotations();
        double halfRotationSize = 1D / halfRotations;
        for (int i = 0; i < halfRotations; i++) {
            double startT = halfRotationSize * i;
            double endT = halfRotationSize * (i + 1);
            
            if (startT != 0) {
                start.set(center);
                coordinator.transformInverted(start, startT);
            }
            
            if (value != null && value <= startT)
                return null;
            
            Double t = searchBetweenSimple(value, center, start, temp, startT, endT, coordinator, 0);
            if (t != null && intersects(toCheck, checkRadiusSquared, center, t, coordinator))
                return t;
        }
        
        return null;
    }
    
    protected Double searchBetweenSimple(@Nullable Double value, Vec3d center, Vec3d start, Vec3d temp, double startT, double endT, CollisionCoordinator coordinator, int steps) {
        if (value != null && value < startT)
            return null;
        
        Boolean beforeFront = isInFront(start);
        if (beforeFront == null)
            return startT;
        
        temp.set(center);
        coordinator.transformInverted(temp, endT);
        Boolean afterFront = isInFront(temp);
        if (afterFront == null)
            return value != null ? Math.min(value, endT) : endT;
        
        if (beforeFront != afterFront) {
            if (steps < accuracySteps) {
                steps++;
                double halfT = (startT + endT) / 2D;
                
                temp.set(center);
                coordinator.transformInverted(temp, halfT);
                
                Boolean halfFront = isInFront(temp);
                if (halfFront == null)
                    return value != null ? Math.min(value, halfT) : halfT;
                
                if (beforeFront != halfFront)
                    return searchBetweenSimple(value, center, start, temp, startT, halfT, coordinator, steps);
                return searchBetweenSimple(value, center, temp, start, halfT, endT, coordinator, steps);
            }
            return startT;
        }
        return null;
    }
    
    public boolean intersects(AABB toCheck, double checkRadiusSquared, Vec3d center, double t, CollisionCoordinator coordinator) {
        Vec3d cachedCenter = new Vec3d(cache.center);
        coordinator.origin.transformPointToWorld(cachedCenter);
        coordinator.transform(cachedCenter, t);
        cachedCenter.sub(center);
        
        if (cachedCenter.lengthSquared() >= checkRadiusSquared + cache.radiusSquared)
            return false;
        
        Matrix4 matrix = coordinator.getInverted(t);
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        
        for (int i = 0; i < BoxCorner.values().length; i++) {
            Vec3d corner = BoxCorner.values()[i].get(toCheck);
            
            coordinator.transform(matrix, corner);
            coordinator.origin.transformPointToFakeWorld(corner);
            
            if (bb.contains(corner.x, corner.y, corner.z))
                return true;
            
            minX = Math.min(minX, corner.x);
            minY = Math.min(minY, corner.y);
            minZ = Math.min(minZ, corner.z);
            maxX = Math.max(maxX, corner.x);
            maxY = Math.max(maxY, corner.y);
            maxZ = Math.max(maxZ, corner.z);
        }
        
        return bb.minX < maxX && bb.maxX > minX && bb.minY < maxY && bb.maxY > minY && bb.minZ < maxZ && bb.maxZ > minZ;
    }
    
    public static CollidingPlane[] getPlanes(AABB box, PlaneCache cache, CollisionCoordinator coordinator) {
        Vec3d[] corners = BoxUtils.getRotatedCorners(box, coordinator.origin);
        
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
            planes[index] = new CollidingPlane(box, Facing.EAST, cache, corners, BoxFace.get(Facing.EAST).corners);
            index++;
        }
        if (west) {
            planes[index] = new CollidingPlane(box, Facing.WEST, cache, corners, BoxFace.get(Facing.WEST).corners);
            index++;
        }
        if (up) {
            planes[index] = new CollidingPlane(box, Facing.UP, cache, corners, BoxFace.get(Facing.UP).corners);
            index++;
        }
        if (down) {
            planes[index] = new CollidingPlane(box, Facing.DOWN, cache, corners, BoxFace.get(Facing.DOWN).corners);
            index++;
        }
        if (south) {
            planes[index] = new CollidingPlane(box, Facing.SOUTH, cache, corners, BoxFace.get(Facing.SOUTH).corners);
            index++;
        }
        if (north) {
            planes[index] = new CollidingPlane(box, Facing.NORTH, cache, corners, BoxFace.get(Facing.NORTH).corners);
            index++;
        }
        
        return planes;
    }
    
    public static Facing getDirection(CollisionCoordinator coordinator, AABB box, Vec3d center) {
        throw new UnsupportedOperationException();
        /*double x = (center.x - box.cache.center.x) / (box.maxX - box.minX);
        double y = (center.y - box.cache.center.y) / (box.maxY - box.minY);
        double z = (center.z - box.cache.center.z) / (box.maxZ - box.minZ);
        
        boolean xy = Math.abs(x) > Math.abs(y);
        boolean xz = Math.abs(x) > Math.abs(z);
        boolean yz = Math.abs(y) > Math.abs(z);
        if (xy && xz)
            return x > 0 ? Facing.EAST : Facing.WEST;
        else if (!xz && !yz)
            return z > 0 ? Facing.SOUTH : Facing.NORTH;
        return y > 0 ? Facing.UP : Facing.DOWN;*/
    }
    
    public static class PlaneCache {
        
        public CollidingPlane[] planes;
        public final Vec3d center;
        public final double radiusSquared;
        
        public PlaneCache(AABB box) {
            this.radiusSquared = (box.minX * box.maxX + box.minY * box.maxY + box.minZ * box.maxZ) * 0.5;
            this.center = new Vec3d(box.minX + (box.maxX - box.minX) * 0.5D, box.minY + (box.maxY - box.minY) * 0.5D, box.minZ + (box.maxZ - box.minZ) * 0.5D);
        }
        
        public boolean isCached() {
            return planes != null;
        }
        
        public void reset() {
            planes = null;
        }
        
    }
    
    public static class PushCache {
        
        public Facing facing;
        public AABB pushBox;
        
        public AABB entityBox;
        public AABB entityBoxOrientated;
        
    }
}
