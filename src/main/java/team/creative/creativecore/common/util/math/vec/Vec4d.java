package team.creative.creativecore.common.util.math.vec;

import team.creative.creativecore.common.util.math.base.Axis;

public class Vec4d extends VecNd<Vec4d> {
    
    public double x;
    public double y;
    public double z;
    public double w;
    
    public Vec4d() {
        super();
    }
    
    public Vec4d(double x, double y, double z, double w) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vec4d(Vec3d vec, double w) {
        this(vec.x, vec.y, vec.z, w);
    }
    
    public Vec4d(Vec4d vec) {
        super(vec);
    }
    
    @Override
    public void set(Vec4d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
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
        case 3:
            return w;
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
    
    public void set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
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
        case 3:
            this.w = value;
            break;
        }
    }
    
    @Override
    public int dimensions() {
        return 4;
    }
    
    @Override
    public Vec4d copy() {
        return new Vec4d(x, y, z, w);
    }
    
    @Override
    public void add(Vec4d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        this.w += vec.w;
    }
    
    @Override
    public void sub(Vec4d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        this.w -= vec.w;
    }
    
    @Override
    public void scale(double scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        this.w *= scale;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3d)
            return ((Vec3d) obj).x == x && ((Vec3d) obj).y == y && ((Vec3d) obj).z == z;
        return false;
    }
    
    @Override
    public boolean epsilonEquals(Vec4d var1, double var2) {
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
        else if ((var3 < 0.0F ? -var3 : var3) > var2)
            return false;
        var3 = this.w - var1.w;
        if (Double.isNaN(var3))
            return false;
        else
            return (var3 < 0.0F ? -var3 : var3) <= var2;
    }
    
    @Override
    public double distance(Vec4d vec) {
        double x = this.x - vec.x;
        double y = this.y - vec.y;
        double z = this.z - vec.z;
        double w = this.w - vec.w;
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }
    
    @Override
    public double distanceSqr(Vec4d vec) {
        double x = this.x - vec.x;
        double y = this.y - vec.y;
        double z = this.z - vec.z;
        double w = this.w - vec.w;
        return x * x + y * y + z * z + w * w;
    }
    
    @Override
    public double length() {
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }
    
    @Override
    public double lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }
    
    @Override
    public double angle(Vec4d vec) {
        double var2 = this.dot(vec) / (this.length() * vec.length());
        if (var2 < -1.0D)
            var2 = -1.0D;
        if (var2 > 1.0D)
            var2 = 1.0D;
        
        return Math.acos(var2);
    }
    
    @Override
    public double dot(Vec4d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z + this.w * vec.w;
    }
    
}
