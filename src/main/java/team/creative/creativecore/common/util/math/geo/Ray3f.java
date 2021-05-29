package team.creative.creativecore.common.util.math.geo;

import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public class Ray3f {
    
    public final Vec3f origin;
    public final Vec3f direction;
    
    public Ray3f(Vec3f start, Vec3f end) {
        this.origin = start;
        this.direction = new Vec3f(end);
        this.direction.sub(origin);
        this.direction.normalize();
    }
    
    public Ray3f(Vec3f origin, Facing facing) {
        this.origin = origin;
        this.direction = new Vec3f();
        direction.set(facing.axis, facing.offset());
    }
    
    public void set(float x, float y, float z, float x2, float y2, float z2) {
        origin.set(x, y, z);
        direction.set(x2, y2, z2);
        direction.sub(origin);
    }
    
    public double getT(Axis axis, double value) {
        return (value - origin.get(axis)) / direction.get(axis);
    }
    
    public Vec3f get(float t) {
        return new Vec3f(origin.x + direction.x * t, origin.y + direction.y * t, origin.z + direction.z * t);
    }
    
}
