package team.creative.creativecore.common.util.math.vec;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.base.Axis;

public class Vec3d extends VecNd<Vec3d> {
    
    public double x;
    public double y;
    public double z;
    
    public Vec3d() {
        super();
    }
    
    public Vec3d(Vec3i vec) {
        this(vec.getX(), vec.getY(), vec.getZ());
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
    
    public Vec3d(Vec3f vec) {
        this(vec.x, vec.y, vec.z);
    }
    
    public Vec3d(Vector3d vec) {
        this(vec.x, vec.y, vec.z);
    }
    
    public Vec3d(Vec3 vec) {
        this(vec.x, vec.y, vec.z);
    }
    
    public Vec3d(Vector3f step) {
        this(step.x(), step.y(), step.z());
    }
    
    public Vec3 toVanilla() {
        return new Vec3(x, y, z);
    }
    
    public BlockPos toBlockPos() {
        return BlockPos.containing(x, y, z);
    }
    
    @Override
    public void set(Vec3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
    
    public void set(Vec3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
    
    @Override
    public double get(int dim) {
        return switch (dim) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> 0;
        };
    }
    
    @Override
    public double get(Axis axis) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
            default -> 0;
        };
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
    
    public double distance(Vec3 vec) {
        return distance(vec.x, vec.y, vec.z);
    }
    
    @Override
    public double distance(Vec3d vec) {
        double x = this.x - vec.x;
        double y = this.y - vec.y;
        double z = this.z - vec.z;
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    @Override
    public double distanceSqr(Vec3d vec) {
        double x = this.x - vec.x;
        double y = this.y - vec.y;
        double z = this.z - vec.z;
        return x * x + y * y + z * z;
    }
    
    public double distance(double x, double y, double z) {
        double posX = this.x - x;
        double posY = this.y - y;
        double posZ = this.z - z;
        return Math.sqrt(posX * posX + posY * posY + posZ * posZ);
    }
    
    public double distanceSqr(double x, double y, double z) {
        double posX = this.x - x;
        double posY = this.y - y;
        double posZ = this.z - z;
        return posX * posX + posY * posY + posZ * posZ;
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
    
    public void transform(Quaterniond quaternion) {
        double xx = quaternion.x * quaternion.x, yy = quaternion.y * quaternion.y, zz = quaternion.z * quaternion.z, ww = quaternion.w * quaternion.w;
        double xy = quaternion.x * quaternion.y, xz = quaternion.x * quaternion.z, yz = quaternion.y * quaternion.z, xw = quaternion.x * quaternion.w;
        double zw = quaternion.z * quaternion.w, yw = quaternion.y * quaternion.w, k = 1 / (xx + yy + zz + ww);
        set(Math.fma((xx - yy - zz + ww) * k, x, Math.fma(2 * (xy - zw) * k, y, (2 * (xz + yw) * k) * z)), Math.fma(2 * (xy + zw) * k, x, Math.fma((yy - xx - zz + ww) * k, y,
            (2 * (yz - xw) * k) * z)), Math.fma(2 * (xz - yw) * k, x, Math.fma(2 * (yz + xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
    }
    
    public void transformInverse(Quaterniond quaternion) {
        double n = 1.0 / Math.fma(quaternion.x, quaternion.x, Math.fma(quaternion.y, quaternion.y, Math.fma(quaternion.z, quaternion.z, quaternion.w * quaternion.w)));
        double qx = quaternion.x * n, qy = quaternion.y * n, qz = quaternion.z * n, qw = quaternion.w * n;
        double xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        double xy = qx * qy, xz = qx * qz, yz = qy * qz, xw = qx * qw;
        double zw = qz * qw, yw = qy * qw, k = 1 / (xx + yy + zz + ww);
        set(Math.fma((xx - yy - zz + ww) * k, x, Math.fma(2 * (xy + zw) * k, y, (2 * (xz - yw) * k) * z)), Math.fma(2 * (xy - zw) * k, x, Math.fma((yy - xx - zz + ww) * k, y,
            (2 * (yz + xw) * k) * z)), Math.fma(2 * (xz + yw) * k, x, Math.fma(2 * (yz - xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
    }
    
}
