package com.creativemd.creativecore.common.utils.math.geo;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.vec.IVecInt;
import com.creativemd.creativecore.common.utils.math.vec.VectorFan;
import com.creativemd.creativecore.common.utils.math.vec.VectorFan.ParallelException;

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
        this.originOne = VectorUtils.get(one, origin);
        this.originTwo = VectorUtils.get(two, origin);
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
        set(one, two, startOne, startTwo, endOne, endTwo);
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
    
    public void set(Axis one, Axis two, double startOne, double startTwo, double endOne, double endTwo) {
        this.one = one;
        this.two = two;
        
        this.originOne = startOne;
        this.originTwo = startTwo;
        this.directionOne = endOne - startOne;
        this.directionTwo = endTwo - startTwo;
    }
    
    public void set(Axis one, Axis two, Vector3f first, Vector3f second) {
        this.one = one;
        this.two = two;
        
        this.originOne = VectorUtils.get(one, first);
        this.originTwo = VectorUtils.get(two, first);
        this.directionOne = VectorUtils.get(one, second) - originOne;
        this.directionTwo = VectorUtils.get(two, second) - originTwo;
    }
    
    public double getT(Axis axis, double value) {
        return (value - getOrigin(axis)) / getDirection(axis);
    }
    
    public double get(Axis axis, double value) {
        Axis other = getOther(axis);
        return getOrigin(other) + getDirection(other) * (value - getOrigin(axis)) / getDirection(axis);
    }
    
    public Vector2d get(double t) {
        return new Vector2d(originOne + directionOne * t, originTwo + directionTwo * t);
    }
    
    public Vector2f getFloat(double t) {
        return new Vector2f((float) (originOne + directionOne * t), (float) (originTwo + directionTwo * t));
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
    
    public boolean isCoordinateOnLine(double one, double two) {
        if (directionOne == 0)
            return originOne == one;
        else if (directionTwo == 0)
            return originTwo == two;
        return get(this.one, one) == two;
    }
    
    public boolean isCoordinateToTheRight(int one, int two) {
        double tempOne = one - originOne;
        double tempTwo = two - originTwo;
        return directionOne * tempTwo - directionTwo * tempOne < 0;
    }
    
    public Boolean isCoordinateToTheRight(double one, double two) {
        double tempOne = one - originOne;
        double tempTwo = two - originTwo;
        double result = directionOne * tempTwo - directionTwo * tempOne;
        if (result > -VectorFan.EPSILON && result < VectorFan.EPSILON)
            return null;
        return result < 0;
    }
    
    public Vector3f intersect(Vector3f start, Vector3f end, float thirdValue) {
        float lineOriginOne = VectorUtils.get(one, start);
        float lineOriginTwo = VectorUtils.get(two, start);
        float lineDirectionOne = VectorUtils.get(one, end) - VectorUtils.get(one, start);
        float lineDirectionTwo = VectorUtils.get(two, end) - VectorUtils.get(two, start);
        
        if (VectorUtils.isZero(directionOne * lineDirectionTwo - directionTwo * lineDirectionOne))
            return null;
        
        Vector3f vec = new Vector3f(thirdValue, thirdValue, thirdValue);
        double t = ((lineOriginTwo - originTwo) * lineDirectionOne + originOne * lineDirectionTwo - lineOriginOne * lineDirectionTwo) / (lineDirectionOne * directionTwo - directionOne * lineDirectionTwo);
        VectorUtils.set(vec, (float) (originOne + t * directionOne), one);
        VectorUtils.set(vec, (float) (originTwo + t * directionTwo), two);
        return vec;
    }
    
    public double intersectWhen(Ray2d line) throws ParallelException {
        if (VectorUtils.isZero(directionOne * line.directionTwo - directionTwo * line.directionOne))
            if (isCoordinateOnLine(line.originOne, line.originTwo))
                throw new ParallelException();
            else
                return -1;
        return ((line.originTwo - originTwo) * line.directionOne + originOne * line.directionTwo - line.originOne * line.directionTwo) / (line.directionOne * directionTwo - directionOne * line.directionTwo);
    }
    
    public Vector3d intersect(Ray2d line, int thirdValue) {
        if (VectorUtils.isZero(directionOne * line.directionTwo - directionTwo * line.directionOne))
            return null;
        
        Vector3d vec = new Vector3d(thirdValue, thirdValue, thirdValue);
        double t = ((line.originTwo - originTwo) * line.directionOne + originOne * line.directionTwo - line.originOne * line.directionTwo) / (line.directionOne * directionTwo - directionOne * line.directionTwo);
        VectorUtils.set(vec, originOne + t * directionOne, one);
        VectorUtils.set(vec, originTwo + t * directionTwo, two);
        return vec;
    }
    
    @Override
    public String toString() {
        return one + "," + two + ",[" + originOne + "," + originTwo + "],[" + directionOne + "," + directionTwo + "]";
    }
}
