package team.creative.creativecore.common.util.math.box;

import java.util.List;

import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.collision.IntersectionHelper;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec2d;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class OBB extends ABB {
    
    /** @return -1 -> value is too small; 0 -> value is inside min and max; 1 ->
     *         value is too large */
    private static int getCornerOffset(double value, double min, double max) {
        if (value <= min)
            return -1;
        else if (value >= max)
            return 1;
        return 0;
    }
    
    public static double calculateDistanceFromPlane(boolean positive, double closestValue, Vec2d vec, double firstAxisValue, double secondAxisValue, double outerCornerAxis) {
        double valueAxis = outerCornerAxis + (firstAxisValue - outerCornerAxis) * vec.x + (secondAxisValue - outerCornerAxis) * vec.y;
        return positive ? valueAxis - closestValue : closestValue - valueAxis;
    }
    
    public IVecOrigin origin;
    
    public OBB(ABB bb, IVecOrigin origin) {
        super(bb);
        this.origin = origin;
    }
    
    public OBB(AABB bb, IVecOrigin origin) {
        super(bb);
        this.origin = origin;
    }
    
    @Override
    public OBB copy() {
        return new OBB(this, origin);
    }
    
    @Override
    public boolean intersectsPrecise(AABB bb) {
        // TODO Proper calculations need to be there otherwise crouching on obbs will not work
        return false;
    }
    
    public double calculateDistanceRotated(AABB other, Axis axis, double offset) {
        boolean positive = offset > 0;
        Facing facing = Facing.get(axis, !positive);
        double closestValue = get(other, facing.opposite());
        
        Axis one = axis.one();
        Axis two = axis.two();
        
        double minOne = min(other, one);
        double minTwo = min(other, two);
        double maxOne = max(other, one);
        double maxTwo = max(other, two);
        
        Vec3d[] corners = getOuterCorner(facing, origin, minOne, minTwo, maxOne, maxTwo);
        
        Vec3d outerCorner = corners[0];
        double outerCornerOne = outerCorner.get(one);
        double outerCornerTwo = outerCorner.get(two);
        double outerCornerAxis = outerCorner.get(axis);
        
        int outerCornerOffsetOne = getCornerOffset(outerCornerOne, minOne, maxOne);
        int outerCornerOffsetTwo = getCornerOffset(outerCornerTwo, minTwo, maxTwo);
        
        if (outerCornerOffsetOne == 0 && outerCornerOffsetTwo == 0) {
            // Hits the outer corner
            if (positive)
                return outerCornerAxis - closestValue;
            return closestValue - outerCornerAxis;
        }
        
        Vec2d[] directions = new Vec2d[3];
        
        double minDistance = Double.MAX_VALUE;
        
        Vec2d[] vectors = { new Vec2d(minOne - outerCornerOne, minTwo - outerCornerTwo), new Vec2d(maxOne - outerCornerOne, minTwo - outerCornerTwo), new Vec2d(maxOne - outerCornerOne, maxTwo - outerCornerTwo), new Vec2d(minOne - outerCornerOne, maxTwo - outerCornerTwo) };
        Vec2d[] vectorsRelative = { new Vec2d(), new Vec2d(), new Vec2d(), new Vec2d() };
        
        directions[0] = new Vec2d(corners[1].get(one) - outerCornerOne, corners[1].get(two) - outerCornerTwo);
        directions[1] = new Vec2d(corners[2].get(one) - outerCornerOne, corners[2].get(two) - outerCornerTwo);
        directions[2] = new Vec2d(corners[3].get(one) - outerCornerOne, corners[3].get(two) - outerCornerTwo);
        
        face_loop: for (int i = 0; i < 3; i++) { // Calculate faces
            
            int indexFirst = i;
            int indexSecond = i == 2 ? 0 : i + 1;

            Vec2d first = directions[indexFirst];
            Vec2d second = directions[indexSecond];
            
            if (first.x == 0 || second.y == 0) {
                int temp = indexFirst;
                indexFirst = indexSecond;
                indexSecond = temp;
                first = directions[indexFirst];
                second = directions[indexSecond];
            }
            
            double firstAxisValue = corners[indexFirst + 1].get(axis);
            double secondAxisValue = corners[indexSecond + 1].get(axis);
            
            boolean allInside = true;
            
            for (int j = 0; j < 4; j++) {
                
                Vec2d vector = vectors[j];
                
                double t = (vector.x * second.y - vector.y * second.x) / (first.x * second.y - first.y * second.x);
                if (Double.isNaN(t) || Double.isInfinite(t))
                    continue face_loop;
                double s = (vector.y - t * first.y) / second.y;
                if (Double.isNaN(s) || Double.isInfinite(s))
                    continue face_loop;
                
                if (t <= 0 || t >= 1 || s <= 0 || s >= 1)
                    allInside = false;
                vectorsRelative[j].set(t, s);
            }
            
            if (allInside) {
                for (int j = 0; j < vectorsRelative.length; j++) {
                    double distance = calculateDistanceFromPlane(positive, closestValue, vectorsRelative[j], firstAxisValue, secondAxisValue, outerCornerAxis);
                    minDistance = Math.min(distance, minDistance);
                }
            } else {
                List<Vec2d> points = IntersectionHelper.cutMinMax(0, 0, 1, 1, vectorsRelative);
                for (int j = 0; j < points.size(); j++) {
                    double distance = calculateDistanceFromPlane(positive, closestValue, points.get(j), firstAxisValue, secondAxisValue, outerCornerAxis);
                    minDistance = Math.min(distance, minDistance);
                }
            }
            
        }
        
        if (minDistance == Double.MAX_VALUE)
            return -1;
        
        return minDistance;
    }
    
    @Override
    public double calculateAxisOffset(Axis axis, Axis one, Axis two, AABB other, double offset) {
        if (offset == 0)
            return offset;
        if (Math.abs(offset) < 1.0E-7D)
            return 0.0D;
        
        double distance = calculateDistanceRotated(other, axis, offset);
        
        if (distance < 0 && !Maths.equals(distance, 0))
            return offset;
        
        if (offset > 0.0D) {
            if (distance < offset)
                return distance;
            return offset;
        } else if (offset < 0.0D) {
            if (-distance > offset)
                return -distance;
            return offset;
        }
        return offset;
    }
    
    @Override
    public String toString() {
        return "OBB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
}
