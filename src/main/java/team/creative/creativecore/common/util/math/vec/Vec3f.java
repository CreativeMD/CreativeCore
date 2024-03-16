package team.creative.creativecore.common.util.math.vec;

import org.joml.Vector3d;
import org.joml.Vector3f;

import team.creative.creativecore.common.util.math.base.Axis;

public class Vec3f extends VecNf<Vec3f> {
    
    public float x;
    public float y;
    public float z;
    
    public Vec3f() {
        super();
    }
    
    public Vec3f(float x, float y, float z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec3f(Vec3f vec) {
        super(vec);
    }
    
    public Vec3f(Vector3f vec) {
        this(vec.x(), vec.y(), vec.z());
    }
    
    public Vec3f(Vec3d vec) {
        this((float) vec.x, (float) vec.y, (float) vec.z);
    }
    
    public Vector3d toVanilla() {
        return new Vector3d(x, y, z);
    }
    
    @Override
    public void set(Vec3f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
    
    @Override
    public float get(Axis axis) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
            default -> 0;
        };
    }
    
    @Override
    public float get(int dim) {
        return switch (dim) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> 0;
        };
    }
    
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public void set(int dim, float value) {
        switch (dim) {
            case 0:
                this.x = value;
                break;
            case 1:
                this.y = value;
                break;
            case 2:
                this.z = value;
                break;
        }
    }
    
    @Override
    public void set(Axis axis, float value) {
        switch (axis) {
            case X:
                this.x = value;
                break;
            case Y:
                this.y = value;
                break;
            case Z:
                this.z = value;
                break;
        }
    }
    
    @Override
    public int dimensions() {
        return 3;
    }
    
    @Override
    public Vec3f copy() {
        return new Vec3f(x, y, z);
    }
    
    @Override
    public void add(Vec3f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }
    
    @Override
    public void sub(Vec3f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }
    
    @Override
    public void scale(double dScale) {
        float scale = (float) dScale;
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3f)
            return ((Vec3f) obj).x == x && ((Vec3f) obj).y == y && ((Vec3f) obj).z == z;
        return false;
    }
    
    @Override
    public boolean epsilonEquals(Vec3f var1, float var2) {
        float var3 = this.x - var1.x;
        if (Float.isNaN(var3))
            return false;
        else if ((var3 < 0.0F ? -var3 : var3) > var2)
            return false;
        var3 = this.y - var1.y;
        if (Float.isNaN(var3))
            return false;
        else if ((var3 < 0.0F ? -var3 : var3) > var2)
            return false;
        var3 = this.z - var1.z;
        if (Float.isNaN(var3))
            return false;
        else
            return (var3 < 0.0F ? -var3 : var3) <= var2;
    }
    
    @Override
    public double distance(Vec3f vec) {
        float x = this.x - vec.x;
        float y = this.y - vec.y;
        float z = this.z - vec.z;
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    @Override
    public double distanceSqr(Vec3f vec) {
        float x = this.x - vec.x;
        float y = this.y - vec.y;
        float z = this.z - vec.z;
        return x * x + y * y + z * z;
    }
    
    @Override
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    @Override
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }
    
    @Override
    public double angle(Vec3f vec) {
        double vDot = this.dot(vec) / (this.length() * vec.length());
        if (vDot < -1.0)
            vDot = -1.0;
        if (vDot > 1.0)
            vDot = 1.0;
        return ((Math.acos(vDot)));
    }
    
    public void cross(Vec3f vec1, Vec3f vec2) {
        this.x = vec1.y * vec2.z - vec1.z * vec2.y;
        this.y = vec2.x * vec1.z - vec2.z * vec1.x;
        this.z = vec1.x * vec2.y - vec1.y * vec2.x;
    }
    
    @Override
    public float dot(Vec3f vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }
    
}
