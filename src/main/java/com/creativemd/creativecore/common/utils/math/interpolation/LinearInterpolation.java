package com.creativemd.creativecore.common.utils.math.interpolation;

import com.creativemd.creativecore.common.utils.math.vec.Vec;

public class LinearInterpolation<T extends Vec> extends Interpolation<T> {
	
	public LinearInterpolation(double[] times, T[] points) {
		super(times, points);
	}
	
	public LinearInterpolation(T... points) {
		super(points);
	}
	
	@Override
	public double valueAt(double mu, int pointIndex, int pointIndexNext, int dim) {
		return (getValue(pointIndexNext, dim) - getValue(pointIndex, dim)) * mu + getValue(pointIndex, dim);
	}
	
}
