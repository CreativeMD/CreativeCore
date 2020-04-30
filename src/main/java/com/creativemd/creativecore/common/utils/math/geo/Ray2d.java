package com.creativemd.creativecore.common.utils.math.geo;

import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.vec.IVecInt;

import net.minecraft.util.EnumFacing.Axis;

public class Ray2d {
	
	public double originOne;
	public double originTwo;
	
	public double directionOne;
	public double directionTwo;
	
	public Axis one;
	public Axis two;
	
	public Ray2d(Axis one, Axis two, Vector3d origin, double directionOne, double directionTwo) {
		this.one = one;
		this.two = two;
		this.originOne = RotationUtils.get(one, origin);
		this.originTwo = RotationUtils.get(two, origin);
		this.directionOne = directionOne;
		this.directionTwo = directionTwo;
	}
	
	public Ray2d(Axis one, Axis two, IVecInt origin, double directionOne, double directionTwo) {
		this.one = one;
		this.two = two;
		this.originOne = origin.get(one);
		this.originTwo = origin.get(two);
		this.directionOne = directionOne;
		this.directionTwo = directionTwo;
	}
	
	public Ray2d(Axis one, Axis two, double startOne, double startTwo, double endOne, double endTwo) {
		this.one = one;
		this.two = two;
		
		this.originOne = startOne;
		this.originTwo = startTwo;
		this.directionOne = endOne - startOne;
		this.directionTwo = endTwo - startTwo;
	}
	
	public double getOrigin(Axis axis) {
		if (one == axis)
			return originOne;
		return originTwo;
	}
	
	public double getDirection(Axis axis) {
		if (one == axis)
			return directionOne;
		return directionTwo;
	}
	
	public Axis getOther(Axis axis) {
		if (one == axis)
			return two;
		return one;
	}
	
	public double get(Axis axis, double value) {
		Axis other = getOther(axis);
		return getOrigin(other) + getDirection(other) * (value - getOrigin(axis)) / getDirection(axis);
	}
	
	public Double getWithLimits(Axis axis, double value) {
		return getWithLimits(axis, value, 0, 1);
	}
	
	public Double getWithLimits(Axis axis, double value, double min, double max) {
		Axis other = getOther(axis);
		double position = (value - getOrigin(axis)) / getDirection(axis);
		if (position < min || position > max)
			return null;
		return getOrigin(other) + getDirection(other) * position;
	}
	
	public boolean isCoordinateOnLine(int one, int two) {
		return get(this.one, one) == two;
	}
	
	public boolean isCoordinateToTheRight(int one, int two) {
		double tempOne = one - originOne;
		double tempTwo = two - originTwo;
		
		// return directionOne * (-tempTwo) + directionTwo * tempOne > 0;
		
		return directionOne * tempTwo - directionTwo * tempOne < 0;
		
		// d=(x−x1)(y2−y1)−(y−y1)(x2−x1)
		// If d<0d<0 then the point lies on one side of the line, and if d>0d>0 then it
		// lies on the other side. If d=0d=0 then the point lies exactly line.
	}
	
	public Vector3d intersect(Ray2d line, int thirdValue) {
		if (directionOne * line.directionTwo - directionTwo * line.directionOne == 0)
			return null;
		
		Vector3d vec = new Vector3d(thirdValue, thirdValue, thirdValue);
		double t = ((line.originTwo - originTwo) * line.directionOne + originOne * line.directionTwo - line.originOne * line.directionTwo) / (line.directionOne * directionTwo - directionOne * line.directionTwo);
		RotationUtils.setValue(vec, originOne + t * directionOne, one);
		RotationUtils.setValue(vec, originTwo + t * directionTwo, two);
		return vec;
	}
}
