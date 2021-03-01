package team.creative.creativecore.common.util.math.vec;

public class Vector2 extends Vector<Vector2> {
    
    public double x;
    public double y;
    
    public Vector2() {
        super();
    }
    
    public Vector2(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }
    
    public Vector2(Vector2 vec) {
        super(vec);
    }
    
    @Override
    public void set(Vector2 vec) {
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
    public Vector2 copy() {
        return new Vector2(x, y);
    }
    
    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }
    
    @Override
    public void add(Vector2 vec) {
        this.x += x;
        this.y += y;
    }
    
    public void sub(double x, double y) {
        this.x = x;
        this.y -= y;
    }
    
    @Override
    public void sub(Vector2 vec) {
        this.x = vec.x;
        this.y -= vec.y;
    }
    
    @Override
    public void scale(double scale) {
        this.x *= scale;
        this.y *= scale;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2)
            return ((Vector2) obj).x == x && ((Vector2) obj).y == y;
        return false;
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
    public double angle(Vector2 vec) {
        double vDot = this.dot(vec) / (this.length() * vec.length());
        if (vDot < -1.0)
            vDot = -1.0;
        if (vDot > 1.0)
            vDot = 1.0;
        return Math.acos(vDot);
    }
    
    @Override
    public void cross(Vector2 vec1, Vector2 vec2) {
        
    }
    
    @Override
    public double dot(Vector2 vec) {
        return x * vec.x + y * vec.y;
    }
    
}
