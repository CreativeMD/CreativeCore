package team.creative.creativecore.common.util.math.vec;

public class Vec1f extends VecNf<Vec1f> {
    
    public float x;
    
    public Vec1f() {
        super();
    }
    
    public Vec1f(float x) {
        super();
        this.x = x;
    }
    
    public Vec1f(Vec1f vec) {
        super(vec);
    }
    
    @Override
    public void set(Vec1f vec) {
        this.x = vec.x;
    }
    
    @Override
    public float get(int dim) {
        if (dim == 0)
            return this.x;
        return 0;
    }
    
    @Override
    public void set(int dim, float value) {
        if (dim == 0)
            this.x = value;
    }
    
    @Override
    public int dimensions() {
        return 1;
    }
    
    @Override
    public Vec1f copy() {
        return new Vec1f(x);
    }
    
    @Override
    public void add(Vec1f vec) {
        this.x += vec.x;
    }
    
    @Override
    public void sub(Vec1f vec) {
        this.x -= vec.x;
    }
    
    @Override
    public void scale(double scale) {
        this.x *= scale;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec1f)
            return ((Vec1f) obj).x == x;
        return false;
    }
    
    @Override
    public boolean epsilonEquals(Vec1f var1, float var2) {
        float var3 = this.x - var1.x;
        if (Float.isNaN(var3))
            return false;
        return (var3 < 0.0F ? -var3 : var3) <= var2;
    }
    
    @Override
    public double distance(Vec1f vec) {
        return Math.abs(x - vec.x);
    }
    
    @Override
    public double distanceSqr(Vec1f vec) {
        float x = this.x - vec.x;
        return x * x;
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
    public double angle(Vec1f vec) {
        return 0;
    }
    
    @Override
    public float dot(Vec1f vec) {
        return x * vec.x;
    }
    
}
