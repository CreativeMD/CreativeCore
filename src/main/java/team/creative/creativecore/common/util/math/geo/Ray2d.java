package team.creative.creativecore.common.util.math.geo;

import org.joml.Vector3d;

import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.geo.VectorFan.ParallelException;
import team.creative.creativecore.common.util.math.vec.Vec2d;
import team.creative.creativecore.common.util.math.vec.Vec2f;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;
import team.creative.creativecore.common.util.math.vec.VectorUtils;

public class Ray2d {
    
    public double originOne;
    public double originTwo;
    
    public double directionOne;
    public double directionTwo;
    
    public Axis one;
    public Axis two;
    
    public Ray2d(Axis one, Axis two, Vec3d origin, double directionOne, double directionTwo) {
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
    
    public void set(Axis one, Axis two, Vec3f first, Vec3f second) {
        this.one = one;
        this.two = two;
        
        this.originOne = first.get(one);
        this.originTwo = first.get(two);
        this.directionOne = second.get(one) - first.get(one);
        this.directionTwo = second.get(two) - first.get(two);
    }
    
    public double getT(Axis axis, double value) {
        return (value - getOrigin(axis)) / getDirection(axis);
    }
    
    public double get(Axis axis, double value) {
        Axis other = getOther(axis);
        return getOrigin(other) + getDirection(other) * (value - getOrigin(axis)) / getDirection(axis);
    }
    
    public Vec2d get(double t) {
        return new Vec2d(originOne + directionOne * t, originTwo + directionTwo * t);
    }
    
    public Vec2f getFloat(double t) {
        return new Vec2f((float) (originOne + directionOne * t), (float) (originTwo + directionTwo * t));
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
            return VectorUtils.equals(originOne, one);
        else if (directionTwo == 0)
            return VectorUtils.equals(originTwo, two);
        return VectorUtils.equals(get(this.one, one), two);
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
    
    public Vec3f intersect(Vec3f start, Vec3f end, float thirdValue) {
        float lineOriginOne = start.get(one);
        float lineOriginTwo = start.get(two);
        float lineDirectionOne = end.get(one) - start.get(one);
        float lineDirectionTwo = end.get(two) - start.get(two);
        
        if (VectorUtils.isZero(directionOne * lineDirectionTwo - directionTwo * lineDirectionOne))
            return null;
        
        Vec3f vec = new Vec3f(thirdValue, thirdValue, thirdValue);
        double t = ((lineOriginTwo - originTwo) * lineDirectionOne + originOne * lineDirectionTwo - lineOriginOne * lineDirectionTwo) / (lineDirectionOne * directionTwo - directionOne * lineDirectionTwo);
        vec.set(one, (float) (originOne + t * directionOne));
        vec.set(two, (float) (originTwo + t * directionTwo));
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
