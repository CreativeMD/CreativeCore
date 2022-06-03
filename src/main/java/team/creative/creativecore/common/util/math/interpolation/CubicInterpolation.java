package team.creative.creativecore.common.util.math.interpolation;

import java.util.List;

import team.creative.creativecore.common.util.math.vec.VecNd;

public class CubicInterpolation<T extends VecNd> extends Interpolation<T> {
    
    public T beginVec;
    public T endVec;
    
    public CubicInterpolation(double[] times, T[] points) {
        super(times, points);
        beginVec = (T) points[0].copy();
        beginVec.sub(points[1]);
        beginVec.add(points[0]);
        endVec = (T) points[points.length - 1].copy();
        endVec.sub(points[points.length - 2]);
        endVec.add(points[points.length - 1]);
    }
    
    public CubicInterpolation(List<T> points) {
        this(null, points, null);
    }
    
    public CubicInterpolation(T before, List<T> points, T after) {
        super(points);
        if (before != null)
            beginVec = before;
        else {
            beginVec = (T) points.get(0).copy();
            beginVec.sub(points.get(1));
            beginVec.add(points.get(0));
        }
        if (after != null)
            endVec = after;
        else {
            endVec = (T) points.get(points.size() - 1).copy();
            endVec.sub(points.get(points.size() - 2));
            endVec.add(points.get(points.size() - 1));
        }
    }
    
    public CubicInterpolation(double[] times, T before, List<T> points, T after) {
        super(times, points);
        if (before != null)
            beginVec = before;
        else {
            beginVec = (T) points.get(0).copy();
            beginVec.sub(points.get(1));
            beginVec.add(points.get(0));
        }
        if (after != null)
            endVec = after;
        else {
            endVec = (T) points.get(points.size() - 1).copy();
            endVec.sub(points.get(points.size() - 2));
            endVec.add(points.get(points.size() - 1));
        }
    }
    
    public CubicInterpolation(T... points) {
        super(points);
        beginVec = (T) points[0].copy();
        beginVec.sub(points[1]);
        beginVec.add(points[0]);
        endVec = (T) points[points.length - 1].copy();
        endVec.sub(points[points.length - 2]);
        endVec.add(points[points.length - 1]);
    }
    
    @Override
    protected double getValue(int index, int dim) {
        if (index < 0)
            return beginVec.get(dim);
        if (index >= points.size())
            return endVec.get(dim);
        return points.get(index).value.get(dim);
    }
    
    @Override
    public double[] estimateDistance() {
        double[] data = new double[points.size()];
        double startTime = points.getFirst().key;
        double endTime = points.getLast().key;
        double pointDuration = (endTime - startTime) / (points.size() - 1);
        int steps = 3;
        double stepDuration = 1 / (steps + 1);
        
        for (int i = 1; i < points.size(); i++) {
            double distance = 0;
            T last = points.get(i - 1).value;
            for (int j = 1; j <= steps; j++) {
                T vec = valueAt((i - 1 + steps * stepDuration) * pointDuration);
                distance += vec.distance(last);
                last = vec;
            }
            distance += points.get(i).value.distance(last);
            
            data[0] += distance;
            data[i] = distance;
        }
        return data;
    }
    
    @Override
    public double valueAt(double mu, int pointIndex, int pointIndexNext, int dim) {
        
        double v0 = getValue(pointIndex - 1, dim);
        double v1 = getValue(pointIndex, dim);
        double v2 = getValue(pointIndexNext, dim);
        double v3 = getValue(pointIndexNext + 1, dim);
        
        double a0, a1, a2, a3, mu2;
        
        mu2 = mu * mu;
        a0 = v3 - v2 - v0 + v1;
        a1 = v0 - v1 - a0;
        a2 = v2 - v0;
        a3 = v1;
        
        return (a0 * mu * mu2 + a1 * mu2 + a2 * mu + a3);
    }
}
