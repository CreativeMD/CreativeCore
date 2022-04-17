package team.creative.creativecore.common.util.math.interpolation;

import java.util.List;

import team.creative.creativecore.common.util.math.vec.VecNd;

public class LinearInterpolation<T extends VecNd> extends Interpolation<T> {
    
    public LinearInterpolation(double[] times, T[] points) {
        super(times, points);
    }
    
    public LinearInterpolation(T... points) {
        super(points);
    }
    
    public LinearInterpolation(List<T> points) {
        super(points);
    }
    
    @Override
    public double valueAt(double mu, int pointIndex, int pointIndexNext, int dim) {
        return (getValue(pointIndexNext, dim) - getValue(pointIndex, dim)) * mu + getValue(pointIndex, dim);
    }
    
}
