package team.creative.creativecore.common.util.math.box;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2d;

import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.geo.VectorFan;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.utils.BooleanUtils;
import team.creative.creativecore.common.util.math.vec.Vec2d;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.VectorUtils;

public class OBB extends ABB {
    
    private static boolean isPointBetween(Vec2d start, Vec2d end, Vec2d between) {
        return Math.abs((end.x - start.x) * (between.y - start.y) - (end.y - start.y) * (between.x - start.x)) < VectorFan.EPSILON;
    }
    
    public static Vec2d[] cut(Vec2d[] coords, DoubleBoundsChecker[] planes) {
        for (int i = 0; i < planes.length; i++) {
            coords = cut(coords, planes[i]);
            
            if (coords == null || coords.length < 3)
                return null;
        }
        return coords;
    }
    
    public static Vec2d[] cut(Vec2d[] coords, DoubleBoundsChecker predicate) {
        boolean allTheSame = true;
        Boolean allValue = null;
        Boolean[] cutted = new Boolean[coords.length];
        for (int i = 0; i < cutted.length; i++) {
            cutted[i] = predicate.test(coords[i]);
            if (allTheSame) {
                if (i == 0)
                    allValue = cutted[i];
                else {
                    if (allValue == null)
                        allValue = cutted[i];
                    else if (allValue != cutted[i] && cutted[i] != null)
                        allTheSame = false;
                }
            }
        }
        
        if (allTheSame) {
            if (allValue == null)
                return null;
            else if (allValue)
                return coords;
            return null;
        }
        
        List<Vec2d> right = new ArrayList<>();
        Boolean beforeCutted = cutted[cutted.length - 1];
        Vec2d beforeVec = coords[coords.length - 1];
        
        for (int i = 0; i < coords.length; i++) {
            Vec2d vec = coords[i];
            
            if (BooleanUtils.isTrue(cutted[i])) {
                if (BooleanUtils.isFalse(beforeCutted)) {
                    //Intersection
                    Vec2d intersection = predicate.intersect(vec, beforeVec);
                    if (intersection != null)
                        right.add(intersection);
                }
                right.add(vec.copy());
            } else if (BooleanUtils.isFalse(cutted[i])) {
                if (BooleanUtils.isTrue(beforeCutted)) {
                    //Intersection
                    Vec2d intersection = predicate.intersect(vec, beforeVec);
                    if (intersection != null)
                        right.add(intersection);
                }
            } else
                right.add(vec.copy());
            
            beforeCutted = cutted[i];
            beforeVec = vec;
        }
        
        if (isPointBetween(right.get(right.size() - 2), right.get(0), right.get(right.size() - 1)))
            right.remove(right.size() - 1);
        
        if (right.size() >= 3 && isPointBetween(right.get(right.size() - 1), right.get(1), right.get(0)))
            right.remove(0);
        
        if (right.size() < 3)
            return null;
        
        return right.toArray(new Vec2d[0]);
    }
    
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
        
        Vector2d[] directions = new Vector2d[3];
        
        double minDistance = Double.MAX_VALUE;
        
        Vec2d[] vectors = { new Vec2d(minOne - outerCornerOne, minTwo - outerCornerTwo), new Vec2d(maxOne - outerCornerOne, minTwo - outerCornerTwo), new Vec2d(maxOne - outerCornerOne, maxTwo - outerCornerTwo), new Vec2d(minOne - outerCornerOne, maxTwo - outerCornerTwo) };
        Vec2d[] vectorsRelative = { new Vec2d(), new Vec2d(), new Vec2d(), new Vec2d() };
        
        directions[0] = new Vector2d(corners[1].get(one) - outerCornerOne, corners[1].get(two) - outerCornerTwo);
        directions[1] = new Vector2d(corners[2].get(one) - outerCornerOne, corners[2].get(two) - outerCornerTwo);
        directions[2] = new Vector2d(corners[3].get(one) - outerCornerOne, corners[3].get(two) - outerCornerTwo);
        
        face_loop: for (int i = 0; i < 3; i++) { // Calculate faces
            
            int indexFirst = i;
            int indexSecond = i == 2 ? 0 : i + 1;
            
            Vector2d first = directions[indexFirst];
            Vector2d second = directions[indexSecond];
            
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
                Vec2d[] points = cut(vectorsRelative, DoubleBoundsChecker.STANDARD_BOUNDS);
                if (points != null)
                    for (int j = 0; j < points.length; j++) {
                        double distance = calculateDistanceFromPlane(positive, closestValue, points[j], firstAxisValue, secondAxisValue, outerCornerAxis);
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
    
    public static interface DoubleBoundsChecker {
        
        public static DoubleBoundsChecker ZERO_X = new DoubleBoundsChecker() {
            
            @Override
            public Boolean test(Vec2d value) {
                if (VectorUtils.equals(value.x, 0))
                    return null;
                return value.x > 0;
            }
            
            @Override
            public Vec2d intersect(Vec2d start, Vec2d end) {
                double directionX = end.x - start.x;
                double directionY = end.y - start.y;
                
                return new Vec2d(0, start.y + directionY * (0 - start.x) / directionX);
            }
            
        };
        
        public static DoubleBoundsChecker ONE_X = new DoubleBoundsChecker() {
            
            @Override
            public Boolean test(Vec2d value) {
                if (VectorUtils.equals(value.x, 1))
                    return null;
                return value.x < 1;
            }
            
            @Override
            public Vec2d intersect(Vec2d start, Vec2d end) {
                double directionX = end.x - start.x;
                double directionY = end.y - start.y;
                
                return new Vec2d(1, start.y + directionY * (1 - start.x) / directionX);
            }
            
        };
        
        public static DoubleBoundsChecker ZERO_Y = new DoubleBoundsChecker() {
            
            @Override
            public Boolean test(Vec2d value) {
                if (VectorUtils.equals(value.y, 0))
                    return null;
                return value.y > 0;
            }
            
            @Override
            public Vec2d intersect(Vec2d start, Vec2d end) {
                double directionX = end.x - start.x;
                double directionY = end.y - start.y;
                
                return new Vec2d(start.x + directionX * (0 - start.y) / directionY, 0);
            }
            
        };
        
        public static DoubleBoundsChecker ONE_Y = new DoubleBoundsChecker() {
            
            @Override
            public Boolean test(Vec2d value) {
                if (VectorUtils.equals(value.y, 1))
                    return null;
                return value.y < 1;
            }
            
            @Override
            public Vec2d intersect(Vec2d start, Vec2d end) {
                double directionX = end.x - start.x;
                double directionY = end.y - start.y;
                
                return new Vec2d(start.x + directionX * (1 - start.y) / directionY, 1);
            }
            
        };
        
        public static DoubleBoundsChecker[] STANDARD_BOUNDS = new DoubleBoundsChecker[] { ZERO_X, ZERO_Y, ONE_X, ONE_Y };
        
        public Boolean test(Vec2d value);
        
        public Vec2d intersect(Vec2d start, Vec2d end);
    }
}
