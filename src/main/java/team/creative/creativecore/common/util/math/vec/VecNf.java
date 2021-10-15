package team.creative.creativecore.common.util.math.vec;

import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.geo.VectorFan;

public abstract class VecNf<T extends VecNf> {
    
    public VecNf() {
        
    }
    
    public VecNf(T vec) {
        set(vec);
    }
    
    public abstract void set(T vec);
    
    public float get(Axis axis) {
        return get(axis.ordinal());
    }
    
    public void set(Axis axis, float value) {
        set(axis.ordinal(), value);
    }
    
    public abstract float get(int dim);
    
    public abstract void set(int dim, float value);
    
    public abstract int dimensions();
    
    public abstract T copy();
    
    public abstract void add(T vec);
    
    public void add(T origin, T vec) {
        set(origin);
        add(vec);
    }
    
    public abstract void sub(T vec);
    
    public void sub(T origin, T vec) {
        set(origin);
        sub(vec);
    }
    
    public abstract void scale(double scale);
    
    public void invert() {
        scale(-1);
    }
    
    @Override
    public abstract boolean equals(Object obj);
    
    public abstract boolean epsilonEquals(T vec, float epsilon);
    
    public boolean epsilonEquals(T vec) {
        return epsilonEquals(vec, VectorFan.EPSILON);
    }
    
    public abstract double distance(T vec);
    
    public abstract double distanceSqr(T vec);
    
    public abstract double length();
    
    public abstract double lengthSquared();
    
    public void normalize() {
        scale(1 / length());
    }
    
    public abstract double angle(T vec);
    
    public abstract float dot(T vec);
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < dimensions(); i++) {
            if (i > 0)
                builder.append(",");
            builder.append(get(i));
        }
        builder.append("]");
        return builder.toString();
    }
    
}
