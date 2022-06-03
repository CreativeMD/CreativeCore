package team.creative.creativecore.common.util.math.interpolation;

import java.util.List;
import java.util.Map.Entry;

import team.creative.creativecore.common.util.math.vec.VecNd;
import team.creative.creativecore.common.util.type.list.Tuple;
import team.creative.creativecore.common.util.type.list.TupleList;

public abstract class Interpolation<T extends VecNd> {
    
    protected TupleList<Double, T> points = new TupleList<>();
    private final Class classOfT;
    
    public Interpolation(double[] times, T[] points) {
        if (points.length < 2)
            throw new IllegalArgumentException("At least two points are needed!");
        
        if (times.length != points.length)
            throw new IllegalArgumentException("Invalid times array!");
        
        this.classOfT = points[0].getClass();
        for (int i = 0; i < points.length; i++)
            this.points.add(times[i], points[i]);
    }
    
    public Interpolation(TupleList<Double, T> points) {
        if (points.size() < 2)
            throw new IllegalArgumentException("At least two points are needed!");
        
        this.classOfT = points.getFirst().value.getClass();
        this.points = new TupleList<Double, T>(points);
    }
    
    public Interpolation(double[] times, List<T> points) {
        if (points.size() < 2)
            throw new IllegalArgumentException("At least two points are needed!");
        
        if (times.length != points.size())
            throw new IllegalArgumentException("Invalid times array!");
        
        this.classOfT = points.get(0).getClass();
        for (int i = 0; i < points.size(); i++)
            this.points.add(times[i], points.get(i));
    }
    
    public Interpolation(List<T> points) {
        if (points.size() < 2)
            throw new IllegalArgumentException("At least two points are needed!");
        
        double time = 0;
        double stepLength = 1D / (points.size() - 1);
        this.classOfT = points.get(0).getClass();
        for (T t : points) {
            this.points.add(time, t);
            time += stepLength;
        }
    }
    
    public Interpolation(T... points) {
        if (points.length < 2)
            throw new IllegalArgumentException("At least two points are needed!");
        
        this.classOfT = points[0].getClass();
        double time = 0;
        double stepLength = 1D / (points.length - 1);
        for (int i = 0; i < points.length; i++) {
            this.points.add(time, points[i]);
            time += stepLength;
        }
    }
    
    protected double getValue(int index, int dim) {
        return points.get(index).value.get(dim);
    }
    
    public T valueAt(double t) {
        Entry<Double, T> firstPoint = null;
        int indexFirst = -1;
        Entry<Double, T> secondPoint = null;
        int indexSecond = -1;
        
        int i = 0;
        for (Tuple<Double, T> pair : points) {
            if (pair.getKey() >= t) {
                if (firstPoint == null) {
                    firstPoint = pair;
                    indexFirst = i;
                } else {
                    secondPoint = pair;
                    indexSecond = i;
                }
                break;
            }
            
            firstPoint = pair;
            indexFirst = i;
            
            i++;
        }
        
        if (secondPoint == null)
            return (T) firstPoint.getValue().copy();
        
        T vec = (T) firstPoint.getValue().copy();
        
        double pointDistance = secondPoint.getKey() - firstPoint.getKey();
        double mu = (t - firstPoint.getKey()) / pointDistance;
        
        for (int dim = 0; dim < vec.dimensions(); dim++)
            vec.set(dim, valueAt(mu, indexFirst, indexSecond, dim));
        
        return vec;
    }
    
    protected T createVec() {
        return (T) VecNd.createEmptyVec(classOfT);
    }
    
    public abstract double valueAt(double mu, int pointIndex, int pointIndexNext, int dim);
    
    public abstract double[] estimateDistance();
    
}
