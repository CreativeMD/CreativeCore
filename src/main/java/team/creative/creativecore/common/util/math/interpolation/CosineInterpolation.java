package team.creative.creativecore.common.util.math.interpolation;

import java.util.List;

import team.creative.creativecore.common.util.math.vec.VecNd;

public class CosineInterpolation<T extends VecNd> extends Interpolation<T> {
    
    public CosineInterpolation(T... points) {
        super(points);
    }
    
    public CosineInterpolation(List<T> points) {
        super(points);
    }
    
    @Override
    public double valueAt(double mu, int pointIndex, int pointIndexNext, int dim) {
        double mu2 = (1 - Math.cos(mu * Math.PI)) / 2;
        return (getValue(pointIndex, dim) * (1 - mu2) + getValue(pointIndexNext, dim) * mu2);
    }
}
