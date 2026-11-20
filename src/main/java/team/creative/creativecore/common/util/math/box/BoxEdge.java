package team.creative.creativecore.common.util.math.box;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;

public enum BoxEdge {
    
    X_UP_SOUTH(Axis.X, Facing.UP, Facing.SOUTH),
    X_UP_NORTH(Axis.X, Facing.UP, Facing.NORTH),
    X_DOWN_SOUTH(Axis.X, Facing.DOWN, Facing.SOUTH),
    X_DOWN_NORTH(Axis.X, Facing.DOWN, Facing.NORTH),
    Y_EAST_SOUTH(Axis.Y, Facing.EAST, Facing.SOUTH),
    Y_EAST_NORTH(Axis.Y, Facing.EAST, Facing.NORTH),
    Y_WEST_SOUTH(Axis.Y, Facing.WEST, Facing.SOUTH),
    Y_WEST_NORTH(Axis.Y, Facing.WEST, Facing.NORTH),
    Z_EAST_UP(Axis.Z, Facing.EAST, Facing.UP),
    Z_EAST_DOWN(Axis.Z, Facing.EAST, Facing.DOWN),
    Z_WESTN_UP(Axis.Z, Facing.WEST, Facing.UP),
    Z_WEST_DOWN(Axis.Z, Facing.WEST, Facing.DOWN);
    
    public static double distanceRayToSegment(Vec3 start, Vec3 end, Vec3 segA, Vec3 segB) {
        Vec3 u = end.subtract(start).normalize();
        Vec3 v = segB.subtract(segA);
        Vec3 w = start.subtract(segA);
        
        double a = u.dot(u); // 1.0 since u is normalized
        double b = u.dot(v);
        double c = v.dot(v);
        double d = u.dot(w);
        double e = v.dot(w);
        
        double denom = a * c - b * b;
        double s, t;
        
        // 1. Compute unconstrained closest parameters
        if (denom < Maths.EPSILON_DOUBLE) { // Ray and edge are nearly parallel
            s = 0.0;
            t = (b > c ? d / b : e / c);
        } else {
            s = (b * e - c * d) / denom;
            t = (a * e - b * d) / denom;
        }
        
        // 2. Clamp segment parameter t to [0, 1] and recompute s
        if (t < 0.0) {
            t = 0.0;
            s = Math.max(0.0, -d / a);
        } else if (t > 1.0) {
            t = 1.0;
            s = Math.max(0.0, (b - d) / a);
        } else if (s < 0.0) { // Clamp ray parameter s >= 0 (in front of player)
            s = 0.0;
            t = Mth.clamp(e / c, 0.0, 1.0);
        }
        
        Vec3 closestOnRay = start.add(u.scale(s));
        Vec3 closestOnSeg = segA.add(v.scale(t));
        
        return closestOnRay.distanceTo(closestOnSeg);
    }
    
    public final Axis axis;
    public final Facing one;
    public final Facing two;
    
    public final BoxCorner[] corners;
    public final int[] cornerIndexes;
    
    private BoxEdge(Axis axis, Facing one, Facing two) {
        this.axis = axis;
        this.one = one;
        this.two = two;
        this.corners = new BoxCorner[] { BoxCorner.getCornerUnsorted(axis.facing(false), one, two), BoxCorner.getCornerUnsorted(axis.facing(true), one, two) };
        this.cornerIndexes = new int[] { corners[0].ordinal(), corners[1].ordinal() };
    }
    
    public BoxCorner first() {
        return corners[0];
    }
    
    public BoxCorner second() {
        return corners[1];
    }
    
}
