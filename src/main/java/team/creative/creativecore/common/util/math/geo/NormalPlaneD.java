package team.creative.creativecore.common.util.math.geo;

import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public class NormalPlaneD {
    
    public final Vec3d normal;
    public final Vec3d origin;
    
    public NormalPlaneD(Vec3d origin, Vec3d normal) {
        this.origin = origin;
        this.normal = new Vec3d(normal);
        this.normal.normalize();
    }
    
    public NormalPlaneD(Vec3f origin, Vec3f normal) {
        this.origin = new Vec3d(origin);
        this.normal = new Vec3d(normal);
        this.normal.normalize();
    }
    
    public NormalPlaneD(Facing facing) {
        this.origin = new Vec3d();
        this.normal = new Vec3d();
        normal.set(facing.axis, facing.offset());
    }
    
    public NormalPlaneD(Axis axis, double value, Facing facing) {
        this.origin = new Vec3d();
        origin.set(axis, value);
        this.normal = new Vec3d();
        normal.set(facing.axis, facing.offset());
    }
    
    public boolean isInvalid() {
        return Double.isNaN(normal.x) || Double.isNaN(normal.y) || Double.isNaN(normal.z);
    }
    
    public Boolean isInFront(Vec3d vec) {
        return isInFront(vec, 1.0E-7D);
    }
    
    public Boolean isInFront(Vec3d vec, double epsilon) {
        Vec3d temp = new Vec3d(vec);
        temp.sub(origin);
        double result = normal.dot(temp);
        if (result < 0 ? (result > -epsilon) : (result < epsilon))
            return null;
        return result > 0;
    }
    
    public Boolean isInFront(Vec3f vec) {
        return isInFront(vec, VectorFan.EPSILON);
    }
    
    public Boolean isInFront(Vec3f vec, float epsilon) {
        Vec3d temp = new Vec3d(vec);
        temp.sub(origin);
        double result = normal.dot(temp);
        if (result < 0 ? (result > -epsilon) : (result < epsilon))
            return null;
        return result > 0;
    }
    
    public boolean cuts(VectorFan strip) {
        boolean front = false;
        boolean back = false;
        for (int i = 0; i < strip.count(); i++) {
            Boolean result = isInFront(strip.get(i));
            
            if (result == null)
                return true;
            
            if (result)
                front = true;
            if (!result)
                back = true;
            
            if (front && back)
                return true;
        }
        return false;
    }
    
    public Vec3d intersect(Vec3d start, Vec3d end) {
        Vec3d lineOrigin = start;
        Vec3d lineDirection = new Vec3d(end);
        lineDirection.sub(lineOrigin);
        lineDirection.normalize();
        
        if (normal.dot(lineDirection) == 0)
            return null;
        
        double t = (normal.dot(origin) - normal.dot(lineOrigin)) / normal.dot(lineDirection);
        Vec3d point = new Vec3d(lineDirection);
        point.scale(t);
        point.add(lineOrigin);
        return point;
    }
    
    public Vec3d intersect(Ray3d ray) {
        if (normal.dot(ray.direction) == 0)
            return null;
        
        double t = (normal.dot(origin) - normal.dot(ray.origin)) / normal.dot(ray.direction);
        Vec3d point = new Vec3d(ray.direction);
        point.scale(t);
        point.add(ray.origin);
        return point;
    }
    
    public Double project(Axis one, Axis two, Axis axis, double valueOne, double valueTwo) {
        Vec3d lineOrigin = new Vec3d();
        lineOrigin.set(one, valueOne);
        lineOrigin.set(two, valueTwo);
        
        Vec3d lineDirection = new Vec3d();
        lineDirection.set(axis, 1);
        
        if (normal.dot(lineDirection) == 0)
            return null;
        
        double t = (normal.dot(origin) - normal.dot(lineOrigin)) / normal.dot(lineDirection);
        return lineOrigin.get(axis) + lineDirection.get(axis) * t;
    }
    
    @Override
    public String toString() {
        return "[o:" + origin + ",n:" + normal + "]";
    }
    
    public NormalPlaneF toFloat() {
        return new NormalPlaneF(new Vec3f(origin), new Vec3f(normal));
    }
    
}
