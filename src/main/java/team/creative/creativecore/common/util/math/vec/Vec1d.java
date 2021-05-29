package team.creative.creativecore.common.util.math.vec;

public class Vec1d extends VecNd<Vec1d> {
    
    public double x;
    
    public Vec1d() {
        super();
    }
    
    public Vec1d(double x) {
        super();
        this.x = x;
    }
    
    public Vec1d(Vec1d vec) {
        super(vec);
    }
    
    @Override
    public void set(Vec1d vec) {
        this.x = vec.x;
    }
    
    @Override
    public double get(int dim) {
        if (dim == 0)
            return this.x;
        return 0;
    }
    
    @Override
    public void set(int dim, double value) {
        if (dim == 0)
            this.x = value;
    }
    
    @Override
    public int dimensions() {
        return 1;
    }
    
    @Override
    public Vec1d copy() {
        return new Vec1d(x);
    }
    
    @Override
    public void add(Vec1d vec) {
        this.x += vec.x;
    }
    
    @Override
    public void sub(Vec1d vec) {
        this.x -= vec.x;
    }
    
    @Override
    public void scale(double scale) {
        this.x *= scale;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec1d)
            return ((Vec1d) obj).x == x;
        return false;
    }
    
    @Override
    public boolean epsilonEquals(Vec1d var1, double var2) {
        double var3 = this.x - var1.x;
        if (Double.isNaN(var3))
            return false;
        return (var3 < 0.0F ? -var3 : var3) <= var2;
    }
    
    @Override
    public double length() {
        return Math.abs(x);
    }
    
    @Override
    public double lengthSquared() {
        return x * x;
    }
    
    @Override
    public double angle(Vec1d vec) {
        return 0;
    }
    
    @Override
    public double dot(Vec1d vec) {
        return 0;
    }
    
}
