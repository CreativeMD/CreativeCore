package team.creative.creativecore.common.util.math.vec;

import net.minecraft.util.math.vector.Vector3d;

public class Vector3 extends Vector<Vector3> {
    
    public double x;
    public double y;
    public double z;
    
    public Vector3() {
        super();
    }
    
    public Vector3(double x, double y, double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3(Vector3 vec) {
        super(vec);
    }
    
    public Vector3(Vector3d vec) {
        this(vec.x, vec.y, vec.z);
    }
    
    public Vector3d toVanilla() {
        return new Vector3d(x, y, z);
    }
    
    @Override
    public void set(Vector3 vec) {
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
    
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
    public Vector3 copy() {
        return new Vector3(x, y, z);
    }
    
    @Override
    public void add(Vector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }
    
    @Override
    public void sub(Vector3 vec) {
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
        if (obj instanceof Vector3)
            return ((Vector3) obj).x == x && ((Vector3) obj).y == y && ((Vector3) obj).z == z;
        return false;
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
    public double angle(Vector3 vec) {
        double vDot = this.dot(vec) / (this.length() * vec.length());
        if (vDot < -1.0)
            vDot = -1.0;
        if (vDot > 1.0)
            vDot = 1.0;
        return ((Math.acos(vDot)));
    }
    
    @Override
    public void cross(Vector3 vec1, Vector3 vec2) {
        this.x = vec1.y * vec2.z - vec1.z * vec2.y;
        this.y = vec2.x * vec1.z - vec2.z * vec1.x;
        this.z = vec1.x * vec2.y - vec1.y * vec2.x;
    }
    
    @Override
    public double dot(Vector3 vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }
    
}
