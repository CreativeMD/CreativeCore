package team.creative.creativecore.common.util.math.vec;

public class Vector1 extends Vector<Vector1> {
    
    public double x;
    
    public Vector1() {
        super();
    }
    
    public Vector1(double x) {
        super();
        this.x = x;
    }
    
    public Vector1(Vector1 vec) {
        super(vec);
    }
    
    @Override
    public void set(Vector1 vec) {
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
    public Vector1 copy() {
        return new Vector1(x);
    }
    
    @Override
    public void add(Vector1 vec) {
        this.x += vec.x;
    }
    
    @Override
    public void sub(Vector1 vec) {
        this.x -= vec.x;
    }
    
    @Override
    public void scale(double scale) {
        this.x *= scale;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector1)
            return ((Vector1) obj).x == x;
        return false;
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
    public double angle(Vector1 vec) {
        return 0;
    }
    
    @Override
    public void cross(Vector1 vec1, Vector1 vec2) {
        
    }
    
    @Override
    public double dot(Vector1 vec) {
        return 0;
    }
    
}
