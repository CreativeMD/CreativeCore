package team.creative.creativecore.common.util.math.vec;

public class Vec2d extends VecNd<Vec2d> {
    
    public double x;
    public double y;
    
    public Vec2d() {
        super();
    }
    
    public Vec2d(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }
    
    public Vec2d(Vec2d vec) {
        super(vec);
    }
    
    @Override
    public void set(Vec2d vec) {
        this.x = vec.x;
        this.y = vec.y;
    }
    
    @Override
    public double get(int dim) {
        if (dim == 0)
            return x;
        else if (dim == 1)
            return y;
        return 0;
    }
    
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void set(int dim, double value) {
        if (dim == 0)
            this.x = value;
        else if (dim == 1)
            this.y = value;
    }
    
    @Override
    public int dimensions() {
        return 2;
    }
    
    @Override
    public Vec2d copy() {
        return new Vec2d(x, y);
    }
    
    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }
    
    @Override
    public void add(Vec2d vec) {
        this.x += x;
        this.y += y;
    }
    
    public void sub(double x, double y) {
        this.x -= x;
        this.y -= y;
    }
    
    @Override
    public void sub(Vec2d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
    }
    
    @Override
    public void scale(double scale) {
        this.x *= scale;
        this.y *= scale;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec2d)
            return ((Vec2d) obj).x == x && ((Vec2d) obj).y == y;
        return false;
    }
    
    @Override
    public boolean epsilonEquals(Vec2d var1, double var2) {
        double var3 = this.x - var1.x;
        if (Double.isNaN(var3))
            return false;
        else if ((var3 < 0.0F ? -var3 : var3) > var2)
            return false;
        var3 = this.y - var1.y;
        if (Double.isNaN(var3))
            return false;
        return (var3 < 0.0F ? -var3 : var3) <= var2;
    }
    
    @Override
    public double distance(Vec2d vec) {
        double x = this.x - vec.x;
        double y = this.y - vec.y;
        return Math.sqrt(x * x + y * y);
    }
    
    @Override
    public double distanceSqr(Vec2d vec) {
        double x = this.x - vec.x;
        double y = this.y - vec.y;
        return x * x + y * y;
    }
    
    @Override
    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    
    @Override
    public double lengthSquared() {
        return x * x + y * y;
    }
    
    @Override
    public double angle(Vec2d vec) {
        double vDot = this.dot(vec) / (this.length() * vec.length());
        if (vDot < -1.0)
            vDot = -1.0;
        if (vDot > 1.0)
            vDot = 1.0;
        return Math.acos(vDot);
    }
    
    @Override
    public double dot(Vec2d vec) {
        return x * vec.x + y * vec.y;
    }
    
}
