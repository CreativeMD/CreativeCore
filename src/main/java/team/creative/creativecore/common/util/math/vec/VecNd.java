package team.creative.creativecore.common.util.math.vec;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.geo.VectorFan;

public abstract class VecNd<T extends VecNd> {
    
    public VecNd() {
        
    }
    
    public VecNd(T vec) {
        set(vec);
    }
    
    public abstract void set(T vec);
    
    public double get(Axis axis) {
        return get(axis.ordinal());
    }
    
    public void set(Axis axis, double value) {
        set(axis.ordinal(), value);
    }
    
    public abstract double get(int dim);
    
    public abstract void set(int dim, double value);
    
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
    
    public abstract boolean epsilonEquals(T vec, double epsilon);
    
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
    
    public abstract double dot(T vec);
    
    public long[] toLong() {
        long[] array = new long[dimensions()];
        for (int i = 0; i < array.length; i++)
            array[i] = Double.doubleToRawLongBits(get(i));
        return array;
    }
    
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
    
    public static VecNd load(long[] array) {
        if (array.length == 1)
            return new Vec1d(Double.longBitsToDouble(array[0]));
        else if (array.length == 2)
            return new Vec2d(Double.longBitsToDouble(array[0]), Double.longBitsToDouble(array[1]));
        else if (array.length == 3)
            return new Vec3d(Double.longBitsToDouble(array[0]), Double.longBitsToDouble(array[1]), Double.longBitsToDouble(array[2]));
        throw new IllegalArgumentException("Invalid long array for vector: " + Arrays.toString(array));
        
    }
    
    public static <T extends VecNd> T createEmptyVec(Class<T> className) {
        try {
            return className.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            CreativeCore.LOGGER.error(e);
        }
        throw new IllegalArgumentException();
    }
}
