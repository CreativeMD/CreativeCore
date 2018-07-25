package com.creativemd.creativecore.common.utils.math.box;

import javax.annotation.Nullable;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.collision.CreativeAxisAlignedBB;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.box.CollidingPlane.PlaneCache;
import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;
import com.creativemd.creativecore.common.utils.math.vec.Ray2d;
import com.google.common.annotations.VisibleForTesting;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OrientatedBoundingBox extends CreativeAxisAlignedBB {
	
	public IVecOrigin origin;
	public PlaneCache cache;
	
	public void buildCache()
	{
		this.cache = new PlaneCache(this);
	}
	
	public OrientatedBoundingBox(IVecOrigin origin, double x1, double y1, double z1, double x2, double y2, double z2) {
		super(x1, y1, z1, x2, y2, z2);
		this.origin = origin;
	}
	
	public OrientatedBoundingBox(IVecOrigin origin, AxisAlignedBB bb) {
		super(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
		this.origin = origin;
	}
	
	@Override
    public OrientatedBoundingBox setMaxY(double y2)
    {
        return new OrientatedBoundingBox(origin, this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ);
    }

	@Override
    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof OrientatedBoundingBox))
        {
            return false;
        }
        else
        {
        	OrientatedBoundingBox axisalignedbb = (OrientatedBoundingBox) p_equals_1_;
        	
        	if(axisalignedbb.origin != origin)
        	{
        		return false;
        	}
        	else if (Double.compare(axisalignedbb.minX, this.minX) != 0)
            {
                return false;
            }
            else if (Double.compare(axisalignedbb.minY, this.minY) != 0)
            {
                return false;
            }
            else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0)
            {
                return false;
            }
            else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0)
            {
                return false;
            }
            else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0)
            {
                return false;
            }
            else
            {
                return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
            }
        }
    }

    @Override
    public OrientatedBoundingBox contract(double x, double y, double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D)
        {
            d0 -= x;
        }
        else if (x > 0.0D)
        {
            d3 -= x;
        }

        if (y < 0.0D)
        {
            d1 -= y;
        }
        else if (y > 0.0D)
        {
            d4 -= y;
        }

        if (z < 0.0D)
        {
            d2 -= z;
        }
        else if (z > 0.0D)
        {
            d5 -= z;
        }

        return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
    }
    
    @Override
    public OrientatedBoundingBox addCoord(double x, double y, double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D)
        {
            d0 += x;
        }
        else if (x > 0.0D)
        {
            d3 += x;
        }

        if (y < 0.0D)
        {
            d1 += y;
        }
        else if (y > 0.0D)
        {
            d4 += y;
        }

        if (z < 0.0D)
        {
            d2 += z;
        }
        else if (z > 0.0D)
        {
            d5 += z;
        }

        return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
    }
    
    @Override
    public OrientatedBoundingBox expand(double x, double y, double z)
    {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
    }
    
    @Override
    public OrientatedBoundingBox expandXyz(double value)
    {
        return this.expand(value, value, value);
    }
    
    @Override
    public AxisAlignedBB intersect(AxisAlignedBB other)
    {
    	if(other instanceof OrientatedBoundingBox)
    	{
    		OrientatedBoundingBox otherBB = (OrientatedBoundingBox) other;
    		if(otherBB.origin == origin)
    		{
    			double d0 = Math.max(this.minX, other.minX);
                double d1 = Math.max(this.minY, other.minY);
                double d2 = Math.max(this.minZ, other.minZ);
                double d3 = Math.min(this.maxX, other.maxX);
                double d4 = Math.min(this.maxY, other.maxY);
                double d5 = Math.min(this.maxZ, other.maxZ);
	            return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
    		}else{
    			OrientatedBoundingBox converted = otherBB.origin.getOrientatedBox(origin.getAxisAlignedBox(this));
    			double d0 = Math.max(converted.minX, other.minX);
                double d1 = Math.max(converted.minY, other.minY);
                double d2 = Math.max(converted.minZ, other.minZ);
                double d3 = Math.min(converted.maxX, other.maxX);
                double d4 = Math.min(converted.maxY, other.maxY);
                double d5 = Math.min(converted.maxZ, other.maxZ);
	            return new OrientatedBoundingBox(otherBB.origin, d0, d1, d2, d3, d4, d5);
    		}
    	}
    	AxisAlignedBB box = origin.getAxisAlignedBox(this);
    	double d0 = Math.max(box.minX, other.minX);
        double d1 = Math.max(box.minY, other.minY);
        double d2 = Math.max(box.minZ, other.minZ);
        double d3 = Math.min(box.maxX, other.maxX);
        double d4 = Math.min(box.maxY, other.maxY);
        double d5 = Math.min(box.maxZ, other.maxZ);
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }
    
    @Override
    public AxisAlignedBB union(AxisAlignedBB other)
    {
    	if(other instanceof OrientatedBoundingBox)
    	{
    		OrientatedBoundingBox otherBB = (OrientatedBoundingBox) other;
    		if(otherBB.origin == origin)
    		{
    			double d0 = Math.min(this.minX, other.minX);
    	        double d1 = Math.min(this.minY, other.minY);
    	        double d2 = Math.min(this.minZ, other.minZ);
    	        double d3 = Math.max(this.maxX, other.maxX);
    	        double d4 = Math.max(this.maxY, other.maxY);
    	        double d5 = Math.max(this.maxZ, other.maxZ);
	            return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
    		}else{
    			OrientatedBoundingBox converted = otherBB.origin.getOrientatedBox(origin.getAxisAlignedBox(this));
    			double d0 = Math.min(converted.minX, other.minX);
    	        double d1 = Math.min(converted.minY, other.minY);
    	        double d2 = Math.min(converted.minZ, other.minZ);
    	        double d3 = Math.max(converted.maxX, other.maxX);
    	        double d4 = Math.max(converted.maxY, other.maxY);
    	        double d5 = Math.max(converted.maxZ, other.maxZ);
	            return new OrientatedBoundingBox(otherBB.origin, d0, d1, d2, d3, d4, d5);
    		}
    	}
    	AxisAlignedBB box = origin.getAxisAlignedBox(this);
    	double d0 = Math.min(box.minX, other.minX);
        double d1 = Math.min(box.minY, other.minY);
        double d2 = Math.min(box.minZ, other.minZ);
        double d3 = Math.max(box.maxX, other.maxX);
        double d4 = Math.max(box.maxY, other.maxY);
        double d5 = Math.max(box.maxZ, other.maxZ);
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    @Override
    public OrientatedBoundingBox offset(double x, double y, double z)
    {
        return new OrientatedBoundingBox(origin, this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }
    
    @Override
    public OrientatedBoundingBox offset(BlockPos pos)
    {
        return new OrientatedBoundingBox(origin, this.minX + (double)pos.getX(), this.minY + (double)pos.getY(), this.minZ + (double)pos.getZ(), this.maxX + (double)pos.getX(), this.maxY + (double)pos.getY(), this.maxZ + (double)pos.getZ());
    }

    @Override
    public OrientatedBoundingBox move(Vec3d vec)
    {
        return this.offset(vec.xCoord, vec.yCoord, vec.zCoord);
    }
    
    /**
     * @return -1 -> value is too small; 0 -> value is inside min and max; 1 -> value is too large
     */
    private static int getCornerOffset(double value, double min, double max)
    {
    	if(value <= min)
    		return -1;
    	else if(value >= max)
    		return 1;
    	return 0;
    }
    
    public static boolean isFurtherOrEqualThan(double value, double toCheck)
    {
    	if(value < 0)
    		return toCheck <= value;
    	return toCheck >= value;
    }
    
    /**
     * @return if result is negative there should be no collision
     */
    public double calculateDistanceRotated(AxisAlignedBB other, Axis axis, double offset)
    {
    	boolean positive = offset > 0;
    	EnumFacing facing = EnumFacing.getFacingFromAxis(!positive ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, axis);
    	double closestValue = getValueOfFacing(other, facing.getOpposite());
    	Vector3d[] corners = BoxUtils.getOuterCorner(facing, origin, this);
    	
    	Axis one = RotationUtils.getDifferentAxisFirst(axis);
    	Axis two = RotationUtils.getDifferentAxisSecond(axis);
    	
    	double minOne = getMin(other, one);
    	double minTwo = getMin(other, two);
    	double maxOne = getMax(other, one);
    	double maxTwo = getMax(other, two);
    	
    	Vector3d outerCorner = corners[0];
    	double outerCornerOne = RotationUtils.get(one, outerCorner);
    	double outerCornerTwo = RotationUtils.get(two, outerCorner);
    	double outerCornerAxis = RotationUtils.get(axis, outerCorner);
    	
    	int outerCornerOffsetOne = getCornerOffset(outerCornerOne, minOne, maxOne);
    	int outerCornerOffsetTwo = getCornerOffset(outerCornerTwo, minTwo, maxTwo);
    	
    	if(outerCornerOffsetOne == 0 && outerCornerOffsetTwo == 0) // Hits the outer corner
    	{
    		if(positive)
    			return RotationUtils.get(axis, outerCorner) - closestValue;
    		return closestValue - RotationUtils.get(axis, outerCorner);
    	}
    	
    	double minDistance = Double.MAX_VALUE;
    	
    	Vector2d[] directions = new Vector2d[3];
    	
    	for (int i = 1; i <= 3; i++) { // Check all lines which connect to the outer corner
    		
    		Vector3d corner = corners[i];
    		
    		Ray2d line = new Ray2d(one, two, outerCorner, RotationUtils.get(one, corner) - outerCornerOne, RotationUtils.get(two, corner) - outerCornerTwo);
    		directions[i-1] = new Vector2d(line.directionOne, line.directionTwo);
    		
    		int cornerOffsetOne = getCornerOffset(RotationUtils.get(one, corner), minOne, maxOne);
    		if(outerCornerOffsetOne != 0 && outerCornerOffsetOne == cornerOffsetOne)
    			continue;
    		
    		int cornerOffsetTwo = getCornerOffset(RotationUtils.get(two, corner), minTwo, maxTwo);
    		if(outerCornerOffsetTwo != 0 && outerCornerOffsetTwo == cornerOffsetTwo)
    			continue;
    		
    		double axisStart = RotationUtils.get(axis, outerCorner);
    		double axisDirection = RotationUtils.get(axis, corner) - axisStart;
    		
    		if(outerCornerOffsetOne == -1)
    		{
    			double coordinateTwo = line.get(one, minOne);
    			if(coordinateTwo > minTwo && coordinateTwo < maxTwo)
    			{ 
    				double valueAxis = axisStart + ((minOne - line.originOne) / line.directionOne) * axisDirection;
    				double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
    				
    				if(distance < 0)
    					return distance;
    				
    				minDistance = Math.min(distance, minDistance);
    			}
    		}
    		else if(outerCornerOffsetOne == 1)
    		{
    			double coordinateTwo = line.get(one, maxOne);
    			if(coordinateTwo > minTwo && coordinateTwo < maxTwo)
    			{ 
    				double valueAxis = axisStart + ((maxOne - line.originOne) / line.directionOne) * axisDirection;
    				double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
    				
    				if(distance < 0)
	    				return distance;
    				
    				minDistance = Math.min(distance, minDistance);
    			}
    		}
    		
    		if(outerCornerOffsetTwo == -1)
    		{
    			double coordinateOne = line.get(two, minTwo);
    			if(coordinateOne > minOne && coordinateOne < maxOne)
    			{ 
    				double valueAxis = axisStart + ((minTwo - line.originTwo) / line.directionTwo) * axisDirection;
    				double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
    				
    				if(distance < 0)
    					return distance;
    				
    				minDistance = Math.min(distance, minDistance);
    			}
    		}
    		else if(outerCornerOffsetTwo == 1)
    		{
    			double coordinateOne = line.get(two, maxTwo);
    			if(coordinateOne > minOne && coordinateOne < maxOne)
    			{ 
    				double valueAxis = axisStart + ((maxTwo - line.originTwo) / line.directionTwo) * axisDirection;
    				double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
    				
    				if(distance < 0)
	    				return distance;
    				
    				minDistance = Math.min(distance, minDistance);
    			}
    		}
		}
    	
		boolean minOneOffset = outerCornerOne > minOne;
		boolean minTwoOffset = outerCornerTwo > minTwo;
		boolean maxOneOffset = outerCornerOne > maxOne;
		boolean maxTwoOffset = outerCornerTwo > maxTwo;
		
		Vector2d[] vectors;
		
		if(minOneOffset == maxOneOffset && minTwoOffset == maxTwoOffset) 
			vectors = new Vector2d[] {new Vector2d((minOneOffset ? maxOne : minOne) - outerCornerOne, (minTwoOffset ? maxTwo : minTwo) - outerCornerTwo)};
		else if(minOneOffset == maxOneOffset)
			vectors = new Vector2d[] {new Vector2d((minOneOffset ? maxOne : minOne) - outerCornerOne, minTwo - outerCornerTwo), new Vector2d((minOneOffset ? maxOne : minOne) - outerCornerOne, maxTwo - outerCornerTwo)};
		else if(minTwoOffset == maxTwoOffset) 
			vectors = new Vector2d[] {new Vector2d(minOne - outerCornerOne, (minTwoOffset ? maxTwo : minTwo) - outerCornerTwo), new Vector2d(maxOne - outerCornerOne, (minTwoOffset ? maxTwo : minTwo) - outerCornerTwo)};
		else
			vectors = new Vector2d[] {}; // that one cannot exist {new Vector2d(minOne, minTwo), new Vector2d(maxOne, minTwo), new Vector2d(minOne, maxTwo), new Vector2d(maxOne, maxTwo)};		
		
		for (int i = 0; i < 3; i++) { // Calculate faces
			
			int indexFirst = i;
			int indexSecond = i == 2 ? 0 : i + 1;
			
			Vector2d first = directions[indexFirst];
			Vector2d second = directions[indexSecond];
			
			if(first.x == 0 || second.y == 0)
			{
				int temp = indexFirst;
				indexFirst = indexSecond;
				indexSecond = temp;
				first = directions[indexFirst];
				second = directions[indexSecond];
			}
			
			for (int j = 0; j < vectors.length; j++) {
				
				Vector2d vector = vectors[j];			
				
				if((isFurtherOrEqualThan(vector.x, first.x) || isFurtherOrEqualThan(vector.x, second.x) || isFurtherOrEqualThan(vector.x, first.x + second.x)) &&
						(isFurtherOrEqualThan(vector.y, first.y) || isFurtherOrEqualThan(vector.y, second.y) || isFurtherOrEqualThan(vector.y, first.y + second.y)))
				{					
					double t = (vector.x*second.y-vector.y*second.x)/(first.x*second.y-first.y*second.x);
					if(t <= 0 || t >= 1 || Double.isNaN(t))
						continue;
					
    				double s = (vector.y-t*first.y)/second.y;
    				if(s <= 0 || s >= 1 || Double.isNaN(s))
						continue;
    				
    				double valueAxis = outerCornerAxis + (RotationUtils.get(axis, corners[indexFirst+1]) - outerCornerAxis) * t + (RotationUtils.get(axis, corners[indexSecond+1]) - outerCornerAxis) * s;
    				double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
    				
    				if(distance < 0)
    					return distance;
    				
    				if(distance > 0.00000000001)
    					distance -= 0.00000000001;
    				
    				minDistance = Math.min(distance, minDistance);
				}
			}
			
		}
    	
		if(minDistance == Double.MAX_VALUE)
			return -1;
    	
    	return minDistance;
    }
    
    public double calculateOffsetRotated(AxisAlignedBB other, Axis axis, double offset)
    {
    	if(offset == 0)
    		return offset;
    	
    	double distance = calculateDistanceRotated(other, axis, offset);
    	
    	if(distance < 0)
    		return offset;
    	
    	if (offset > 0.0D)
        {
            if (distance < offset)
	            return distance;
            return offset;
        }
        else if (offset < 0.0D)
        {
            if (-distance > offset)
            	return -distance;
            return offset;
        }
        return offset;
    }
    
    @Override
    public double calculateYOffsetStepUp(AxisAlignedBB other, AxisAlignedBB otherY, double offset)
    {
    	double newOffset = calculateYOffset(otherY, offset);
    	
    	if(offset > 0)
    	{
	    	if(newOffset < offset)
	    		return newOffset / 2;
    	}
    	else
    	{
    		if(newOffset > offset)
    			return newOffset / 2;
    	}
    	
    	return newOffset;
    }
    
    public double getMaxTranslated(Axis axis)
    {
    	return getMax(axis) + RotationUtils.get(axis, origin.translation());
    }
    
    public double getMinTranslated(Axis axis)
    {
    	return getMin(axis) + RotationUtils.get(axis, origin.translation());
    }
    
    public double calculateOffset(AxisAlignedBB other, Axis axis, double offset)
    {
    	if(other instanceof OrientatedBoundingBox)
    	{
	    	if(((OrientatedBoundingBox) other).origin == origin)
	    	{
	    		switch(axis)
	    		{
	    		case X:
	    			return super.calculateXOffset(other, offset);
	    		case Y:
	    			return super.calculateYOffset(other, offset);
	    		case Z:
	    			return super.calculateZOffset(other, offset);
	    		}
	    	}
	    	
	    	other = ((OrientatedBoundingBox) other).origin.getAxisAlignedBox(other);
    	}
    	
    	if(origin.isRotated())
    		return calculateOffsetRotated(other, axis, offset);
    	
    	Axis one = RotationUtils.getDifferentAxisFirst(axis);
    	Axis two = RotationUtils.getDifferentAxisSecond(axis);
    	
    	if (getMax(other, one) > getMinTranslated(one) && getMin(other, one) < getMaxTranslated(one) && getMax(other, two) > getMinTranslated(two) && getMin(other, two) < getMaxTranslated(two))
        {
            if (offset > 0.0D && getMax(other, axis) <= getMinTranslated(axis))
            {
                double d1 = getMinTranslated(axis) - getMax(other, axis);

                if (d1 < offset)
                {
                    offset = d1;
                }
            }
            else if (offset < 0.0D && getMin(other, axis) >= getMaxTranslated(one))
            {
                double d0 = getMaxTranslated(one) - getMin(other, axis);

                if (d0 > offset)
                {
                    offset = d0;
                }
            }

            return offset;
        }
        
        return offset;
    }

    @Override
    public double calculateXOffset(AxisAlignedBB other, double offsetX)
    {
    	return calculateOffset(other, Axis.X, offsetX);
    }

    @Override
    public double calculateYOffset(AxisAlignedBB other, double offsetY)
    {
    	return calculateOffset(other, Axis.Y, offsetY);
    }

    @Override
    public double calculateZOffset(AxisAlignedBB other, double offsetZ)
    {	
    	return calculateOffset(other, Axis.Z, offsetZ);
    }

    @Override
    public boolean intersectsWith(AxisAlignedBB other)
    {
    	if(other instanceof OrientatedBoundingBox)
    	{
    		if(((OrientatedBoundingBox) other).origin == origin)
    			return this.minX < other.maxX && this.maxX > other.minX && this.minY < other.maxY && this.maxY > other.minY && this.minZ < other.maxZ && this.maxZ > other.minZ;
			else
			{
				OrientatedBoundingBox converted = ((OrientatedBoundingBox) other).origin.getOrientatedBox(origin.getAxisAlignedBox(this));
				return converted.minX < other.maxX && converted.maxX > other.minX && converted.minY < other.maxY && converted.maxY > other.minY && converted.minZ < other.maxZ && converted.maxZ > other.minZ;
			}
		}
    	
        return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }
    
    @Override
    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
    	AxisAlignedBB box = origin.getAxisAlignedBox(this);
    	return box.minX < maxX && box.maxX > minX && box.minY < maxY && box.maxY > minY && box.minZ < maxZ && box.maxZ > minZ;
    }

    /*@Override
    public boolean contains(Vec3d vec)
    {
        if (vec.x > this.getRealMinX() && vec.x < this.getRealMaxX())
        {
            if (vec.y > this.getRealMinY() && vec.y < this.getRealMaxY())
            {
                return vec.z > this.getRealMinZ() && vec.z < this.getRealMaxZ();
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }*/

    /*@Override
    @Nullable
    public RayTraceResult calculateIntercept(Vec3d vecA, Vec3d vecB)
    {
        Vec3d vec3d = this.collideWithXPlane2(this.getRealMinX(), vecA, vecB);
        EnumFacing enumfacing = EnumFacing.WEST;
        Vec3d vec3d1 = this.collideWithXPlane2(this.getRealMaxX(), vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.EAST;
        }

        vec3d1 = this.collideWithYPlane2(this.getRealMinY(), vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.DOWN;
        }

        vec3d1 = this.collideWithYPlane2(this.getRealMaxY(), vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.UP;
        }

        vec3d1 = this.collideWithZPlane2(this.getRealMinZ(), vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.NORTH;
        }

        vec3d1 = this.collideWithZPlane2(this.getRealMaxZ(), vecA, vecB);

        if (vec3d1 != null && this.isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.SOUTH;
        }

        return vec3d == null ? null : new RayTraceResult(vec3d, enumfacing);
    }

    @Nullable
    @VisibleForTesting
    protected Vec3d collideWithXPlane2(double p_186671_1_, Vec3d p_186671_3_, Vec3d p_186671_4_)
    {
        Vec3d vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_);
        return vec3d != null && this.intersectsWithYZ(vec3d) ? vec3d : null;
    }

    @Nullable
    @VisibleForTesting
    protected Vec3d collideWithYPlane2(double p_186663_1_, Vec3d p_186663_3_, Vec3d p_186663_4_)
    {
        Vec3d vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_);
        return vec3d != null && this.intersectsWithXZ(vec3d) ? vec3d : null;
    }

    @Nullable
    @VisibleForTesting
    protected Vec3d collideWithZPlane2(double p_186665_1_, Vec3d p_186665_3_, Vec3d p_186665_4_)
    {
        Vec3d vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_);
        return vec3d != null && this.intersectsWithXY(vec3d) ? vec3d : null;
    }

    @Override
    @VisibleForTesting
    public boolean intersectsWithYZ(Vec3d vec)
    {
        return vec.y >= this.getRealMinY() && vec.y <= this.getRealMaxY() && vec.z >= this.getRealMinZ() && vec.z <= this.getRealMaxZ();
    }

    @Override
    @VisibleForTesting
    public boolean intersectsWithXZ(Vec3d vec)
    {
        return vec.x >= this.getRealMinX() && vec.x <= this.getRealMaxX() && vec.z >= this.getRealMinZ() && vec.z <= this.getRealMaxZ();
    }

    @Override
    @VisibleForTesting
    public boolean intersectsWithXY(Vec3d vec)
    {
        return vec.x >= this.getRealMinX() && vec.x <= this.getRealMaxX() && vec.y >= this.getRealMinY() && vec.y <= this.getRealMaxY();
    }*/

    @Override
    public String toString()
    {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
    
    public Vector3d getCenter3d()
    {
        return new Vector3d(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
    }
    
    public double getPushOutScale(double minScale, OrientatedBoundingBox fakeBox, AxisAlignedBB originalBox, Vector3d pushVec, Vector3d pushVecInv, @Nullable BoxPlane xPlane, @Nullable BoxPlane yPlane, @Nullable BoxPlane zPlane)
    {
    	double scale = Double.MAX_VALUE;
    	
    	boolean pushX = pushVec.x != 0;
    	boolean pushY = pushVec.y != 0;
    	boolean pushZ = pushVec.z != 0;
    	
    	if(pushX)
    		if(pushVec.x > 0)
    			scale = Math.min(scale, (this.maxX - fakeBox.minX) / pushVec.x);
    		else
    			scale = Math.min(scale, (this.minX - fakeBox.maxX) / pushVec.x);
    	
    	if(pushY)
    		if(pushVec.y > 0)
    			scale = Math.min(scale, (this.maxY - fakeBox.minY) / pushVec.y);
    		else
    			scale = Math.min(scale, (this.minY - fakeBox.maxY) / pushVec.y);
    	
    	if(pushZ)
    		if(pushVec.z > 0)
    			scale = Math.min(scale, (this.maxZ - fakeBox.minZ) / pushVec.z);
    		else
    			scale = Math.min(scale, (this.minZ - fakeBox.maxZ) / pushVec.z);
    	
    	if(scale <= minScale)
    		return minScale;
    	
    	/*for(BoxCorner corner : BoxCorner.values())
    	{
    		if(corner.x.getAxisDirection() == AxisDirection.POSITIVE)
    		{
    			if(pushVec.x < 0)
    				continue;
    		}
    		else
    		{
    			if(pushVec.x > 0)
    				continue;
    		}
    		
    		if(corner.y.getAxisDirection() == AxisDirection.POSITIVE)
    		{
    			if(pushVec.y < 0)
    				continue;
    		}
    		else
    		{
    			if(pushVec.y > 0)
    				continue;
    		}
    		
    		if(corner.z.getAxisDirection() == AxisDirection.POSITIVE)
    		{
    			if(pushVec.z < 0)
    				continue;
    		}
    		else
    		{
    			if(pushVec.z > 0)
    				continue;
    		}
    		
    		Vector3d cornerVec = getCornerVector3d(corner);
    		
    		if(xPlane != null)
    		{
    			double tempScale = xPlane.getIntersectingScale(cornerVec, pushVecInv);
    			if(tempScale <= minScale)
    				return minScale;
    			scale = Math.min(scale, tempScale);
    		}
    		
    		if(yPlane != null)
    		{
    			double tempScale = yPlane.getIntersectingScale(cornerVec, pushVecInv);
    			if(tempScale <= minScale)
    				return minScale;
    			scale = Math.min(scale, tempScale);
    		}
    		
    		if(zPlane != null)
    		{
    			double tempScale = zPlane.getIntersectingScale(cornerVec, pushVecInv);
    			if(tempScale <= minScale)
    				return minScale;
    			scale = Math.min(scale, tempScale);
    		}
    		
    	}*/
    	
    	return scale;
    	
    }
    
}
