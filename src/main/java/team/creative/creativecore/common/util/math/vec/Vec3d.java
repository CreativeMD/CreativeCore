package team.creative.creativecore.common.util.math.vec;

import net.minecraft.util.math.vector.Vector3d;
import team.creative.creativecore.common.util.math.base.Axis;

public class Vec3d extends VecNd<Vec3d> {
    
    public double x;
    public double y;
    public double z;
    
    public Vec3d() {
        super();
    }
    
    public Vec3d(double x, double y, double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec3d(Vec3d vec) {
        super(vec);
    }
    
    public Vec3d(Vector3d vec) {
        this(vec.x, vec.y, vec.z);
    }
    
    public Vector3d toVanilla() {
        return new Vector3d(x, y, z);
    }
    
    @Override
    public void set(Vec3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
    
    @Override
    public double get(int dim) {
        switch (dim) {
        case 0:
            return x;
        case 1:
            return y;
        case 2:
            return z;
        default:
            return 0;
        }
    }
    
    @Override
    public double get(Axis axis) {
        switch (axis) {
        case X:
            return x;
        case Y:
            return y;
        case Z:
            return z;
        default:
            return 0;
        }
    }
    
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public void set(Axis axis, double value) {
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
    public void set(int dim, double value) {
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
    public int dimensions() {
        return 3;
    }
    
    @Override
    public Vec3d copy() {
        return new Vec3d(x, y, z);
    }
    
    @Override
    public void add(Vec3d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }
    
    @Override
    public void sub(Vec3d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }
    
    @Override
    public void scale(double scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3d)
            return ((Vec3d) obj).x == x && ((Vec3d) obj).y == y && ((Vec3d) obj).z == z;
        return false;
    }
    
    @Override
    public boolean epsilonEquals(Vec3d var1, double var2) {
        double var3 = this.x - var1.x;
        if (Double.isNaN(var3))
            return false;
        else if ((var3 < 0.0F ? -var3 : var3) > var2)
            return false;
        var3 = this.y - var1.y;
        if (Double.isNaN(var3))
            return false;
        else if ((var3 < 0.0F ? -var3 : var3) > var2)
            return false;
        var3 = this.z - var1.z;
        if (Double.isNaN(var3))
            return false;
        else
            return (var3 < 0.0F ? -var3 : var3) <= var2;
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
    public double angle(Vec3d vec) {
        double vDot = this.dot(vec) / (this.length() * vec.length());
        if (vDot < -1.0)
            vDot = -1.0;
        if (vDot > 1.0)
            vDot = 1.0;
        return ((Math.acos(vDot)));
    }
    
    public void cross(Vec3d vec1, Vec3d vec2) {
        this.x = vec1.y * vec2.z - vec1.z * vec2.y;
        this.y = vec2.x * vec1.z - vec2.z * vec1.x;
        this.z = vec1.x * vec2.y - vec1.y * vec2.x;
    }
    
    @Override
    public double dot(Vec3d vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }
    
}
