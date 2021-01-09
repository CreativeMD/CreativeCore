package com.creativemd.creativecore.common.utils.math.interpolation;

import com.creativemd.creativecore.common.utils.math.vec.Vec;

public class CubicInterpolation<T extends Vec> extends Interpolation<T> {
    
    public T beginVec;
    public T endVec;
    
    public CubicInterpolation(double[] times, T[] points) {
        super(times, points);
        beginVec = (T) points[0].add(points[0].sub(points[1]));
        endVec = (T) points[points.length - 1].add(points[points.length - 1].sub(points[points.length - 2]));
    }
    
    public CubicInterpolation(T... points) {
        super(points);
        beginVec = (T) points[0].add(points[0].sub(points[1]));
        endVec = (T) points[points.length - 1].add(points[points.length - 1].sub(points[points.length - 2]));
    }
    
    @Override
    protected double getValue(int index, int dim) {
        if (index < 0)
            return beginVec.getValueByDim(dim);
        if (index >= points.size())
            return endVec.getValueByDim(dim);
        return pointVecs.get(index).getValueByDim(dim);
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
