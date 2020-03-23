package com.creativemd.creativecore.common.utils.math.box;

import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.collision.CollidingPlane.PlaneCache;
import com.creativemd.creativecore.common.utils.math.collision.IntersectionHelper;
import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class OrientatedBoundingBox extends CreativeAxisAlignedBB {
	
	public IVecOrigin origin;
	public PlaneCache cache;
	
	public void buildCache() {
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
	public OrientatedBoundingBox setMaxY(double y2) {
		return new OrientatedBoundingBox(origin, this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ);
	}
	
	@Override
	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof OrientatedBoundingBox)) {
			return false;
		} else {
			OrientatedBoundingBox axisalignedbb = (OrientatedBoundingBox) p_equals_1_;
			
			if (axisalignedbb.origin != origin) {
				return false;
			} else if (Double.compare(axisalignedbb.minX, this.minX) != 0) {
				return false;
			} else if (Double.compare(axisalignedbb.minY, this.minY) != 0) {
				return false;
			} else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0) {
				return false;
			} else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0) {
				return false;
			} else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0) {
				return false;
			} else {
				return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
			}
		}
	}
	
	@Override
	public OrientatedBoundingBox contract(double x, double y, double z) {
		double d0 = this.minX;
		double d1 = this.minY;
		double d2 = this.minZ;
		double d3 = this.maxX;
		double d4 = this.maxY;
		double d5 = this.maxZ;
		
		if (x < 0.0D) {
			d0 -= x;
		} else if (x > 0.0D) {
			d3 -= x;
		}
		
		if (y < 0.0D) {
			d1 -= y;
		} else if (y > 0.0D) {
			d4 -= y;
		}
		
		if (z < 0.0D) {
			d2 -= z;
		} else if (z > 0.0D) {
			d5 -= z;
		}
		
		return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
	}
	
	@Override
	public OrientatedBoundingBox expand(double x, double y, double z) {
		double d0 = this.minX;
		double d1 = this.minY;
		double d2 = this.minZ;
		double d3 = this.maxX;
		double d4 = this.maxY;
		double d5 = this.maxZ;
		
		if (x < 0.0D) {
			d0 += x;
		} else if (x > 0.0D) {
			d3 += x;
		}
		
		if (y < 0.0D) {
			d1 += y;
		} else if (y > 0.0D) {
			d4 += y;
		}
		
		if (z < 0.0D) {
			d2 += z;
		} else if (z > 0.0D) {
			d5 += z;
		}
		
		return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
	}
	
	@Override
	public OrientatedBoundingBox grow(double x, double y, double z) {
		double d0 = this.minX - x;
		double d1 = this.minY - y;
		double d2 = this.minZ - z;
		double d3 = this.maxX + x;
		double d4 = this.maxY + y;
		double d5 = this.maxZ + z;
		return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
	}
	
	@Override
	public OrientatedBoundingBox grow(double value) {
		return this.grow(value, value, value);
	}
	
	@Override
	public AxisAlignedBB intersect(AxisAlignedBB other) {
		if (other instanceof OrientatedBoundingBox) {
			OrientatedBoundingBox otherBB = (OrientatedBoundingBox) other;
			if (otherBB.origin == origin) {
				double d0 = Math.max(this.minX, other.minX);
				double d1 = Math.max(this.minY, other.minY);
				double d2 = Math.max(this.minZ, other.minZ);
				double d3 = Math.min(this.maxX, other.maxX);
				double d4 = Math.min(this.maxY, other.maxY);
				double d5 = Math.min(this.maxZ, other.maxZ);
				return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
			} else {
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
	public AxisAlignedBB union(AxisAlignedBB other) {
		if (other instanceof OrientatedBoundingBox) {
			OrientatedBoundingBox otherBB = (OrientatedBoundingBox) other;
			if (otherBB.origin == origin) {
				double d0 = Math.min(this.minX, other.minX);
				double d1 = Math.min(this.minY, other.minY);
				double d2 = Math.min(this.minZ, other.minZ);
				double d3 = Math.max(this.maxX, other.maxX);
				double d4 = Math.max(this.maxY, other.maxY);
				double d5 = Math.max(this.maxZ, other.maxZ);
				return new OrientatedBoundingBox(origin, d0, d1, d2, d3, d4, d5);
			} else {
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
	public OrientatedBoundingBox offset(double x, double y, double z) {
		return new OrientatedBoundingBox(origin, this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}
	
	@Override
	public OrientatedBoundingBox offset(BlockPos pos) {
		return new OrientatedBoundingBox(origin, this.minX + pos.getX(), this.minY + pos.getY(), this.minZ + pos.getZ(), this.maxX + pos.getX(), this.maxY + pos.getY(), this.maxZ + pos.getZ());
	}
	
	@Override
	public OrientatedBoundingBox offset(Vec3d vec) {
		return this.offset(vec.x, vec.y, vec.z);
	}
	
	/** @return -1 -> value is too small; 0 -> value is inside min and max; 1 ->
	 *         value is too large */
	private static int getCornerOffset(double value, double min, double max) {
		if (value <= min)
			return -1;
		else if (value >= max)
			return 1;
		return 0;
	}
	
	/** @return if result is negative there should be no collision */
	public double calculateDistanceRotated(AxisAlignedBB other, Axis axis, double offset) {
		boolean positive = offset > 0;
		EnumFacing facing = EnumFacing.getFacingFromAxis(!positive ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, axis);
		double closestValue = getValueOfFacing(other, facing.getOpposite());
		
		Axis one = RotationUtils.getDifferentAxisFirst(axis);
		Axis two = RotationUtils.getDifferentAxisSecond(axis);
		
		double minOne = getMin(other, one);
		double minTwo = getMin(other, two);
		double maxOne = getMax(other, one);
		double maxTwo = getMax(other, two);
		
		Vector3d[] corners = BoxUtils.getOuterCorner(facing, origin, this, minOne, minTwo, maxOne, maxTwo);
		
		Vector3d outerCorner = corners[0];
		double outerCornerOne = RotationUtils.get(one, outerCorner);
		double outerCornerTwo = RotationUtils.get(two, outerCorner);
		double outerCornerAxis = RotationUtils.get(axis, outerCorner);
		
		int outerCornerOffsetOne = getCornerOffset(outerCornerOne, minOne, maxOne);
		int outerCornerOffsetTwo = getCornerOffset(outerCornerTwo, minTwo, maxTwo);
		
		if (outerCornerOffsetOne == 0 && outerCornerOffsetTwo == 0) {
			// Hits the outer corner
			if (positive)
				return RotationUtils.get(axis, outerCorner) - closestValue;
			return closestValue - RotationUtils.get(axis, outerCorner);
		}
		
		Vector2d[] directions = new Vector2d[3];
		
		/* Calculate line intersection, not necessary anymore
		
		for (int i = 1; i <= 3; i++) { // Check all lines which connect to the outer corner
			
			Vector3d corner = corners[i];
			
			Ray2d line = new Ray2d(one, two, outerCorner, RotationUtils.get(one, corner) - outerCornerOne, RotationUtils.get(two, corner) - outerCornerTwo);
			directions[i - 1] = new Vector2d(line.directionOne, line.directionTwo);
			
			if (outerCornerOffsetOne != 0 && outerCornerOffsetOne == getCornerOffset(RotationUtils.get(one, corner), minOne, maxOne))
				continue;
			
			if (outerCornerOffsetTwo != 0 && outerCornerOffsetTwo == getCornerOffset(RotationUtils.get(two, corner), minTwo, maxTwo))
				continue;
			
			double axisStart = RotationUtils.get(axis, outerCorner);
			double axisDirection = RotationUtils.get(axis, corner) - axisStart;
			
			if (outerCornerOffsetOne == -1) {
				Double coordinateTwo = line.getWithLimits(one, minOne);
				if (coordinateTwo != null && coordinateTwo > minTwo && coordinateTwo < maxTwo) {
					double valueAxis = axisStart + ((minOne - line.originOne) / line.directionOne) * axisDirection;
					double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
					
					if (distance < 0)
						return distance;
					
					minDistance = Math.min(distance, minDistance);
				}
			} else if (outerCornerOffsetOne == 1) {
				Double coordinateTwo = line.getWithLimits(one, maxOne);
				if (coordinateTwo != null && coordinateTwo > minTwo && coordinateTwo < maxTwo) {
					double valueAxis = axisStart + ((maxOne - line.originOne) / line.directionOne) * axisDirection;
					double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
					
					if (distance < 0)
						return distance;
					
					minDistance = Math.min(distance, minDistance);
				}
			}
			
			if (outerCornerOffsetTwo == -1) {
				Double coordinateOne = line.getWithLimits(two, minTwo);
				if (coordinateOne != null && coordinateOne > minOne && coordinateOne < maxOne) {
					double valueAxis = axisStart + ((minTwo - line.originTwo) / line.directionTwo) * axisDirection;
					double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
					
					if (distance < 0)
						return distance;
					
					minDistance = Math.min(distance, minDistance);
				}
			} else if (outerCornerOffsetTwo == 1) {
				Double coordinateOne = line.getWithLimits(two, maxTwo);
				if (coordinateOne != null && coordinateOne > minOne && coordinateOne < maxOne) {
					double valueAxis = axisStart + ((maxTwo - line.originTwo) / line.directionTwo) * axisDirection;
					double distance = positive ? valueAxis - closestValue : closestValue - valueAxis;
					
					if (distance < 0)
						return distance;
					
					minDistance = Math.min(distance, minDistance);
				}
			}
		}
		
		boolean minOneOffset = outerCornerOne > minOne;
		boolean minTwoOffset = outerCornerTwo > minTwo;
		boolean maxOneOffset = outerCornerOne > maxOne;
		boolean maxTwoOffset = outerCornerTwo > maxTwo;*/
		
		double minDistance = Double.MAX_VALUE;
		
		Vector2d[] vectors = { new Vector2d(minOne - outerCornerOne, minTwo - outerCornerTwo), new Vector2d(maxOne - outerCornerOne, minTwo - outerCornerTwo), new Vector2d(maxOne - outerCornerOne, maxTwo - outerCornerTwo), new Vector2d(minOne - outerCornerOne, maxTwo - outerCornerTwo) };
		Vector2d[] vectorsRelative = { new Vector2d(), new Vector2d(), new Vector2d(), new Vector2d() };
		
		directions[0] = new Vector2d(RotationUtils.get(one, corners[1]) - outerCornerOne, RotationUtils.get(two, corners[1]) - outerCornerTwo);
		directions[1] = new Vector2d(RotationUtils.get(one, corners[2]) - outerCornerOne, RotationUtils.get(two, corners[2]) - outerCornerTwo);
		directions[2] = new Vector2d(RotationUtils.get(one, corners[3]) - outerCornerOne, RotationUtils.get(two, corners[3]) - outerCornerTwo);
		
		face_loop: for (int i = 0; i < 3; i++) { // Calculate faces
			
			int indexFirst = i;
			int indexSecond = i == 2 ? 0 : i + 1;
			
			Vector2d first = directions[indexFirst];
			Vector2d second = directions[indexSecond];
			
			if (first.x == 0 || second.y == 0) {
				int temp = indexFirst;
				indexFirst = indexSecond;
				indexSecond = temp;
				first = directions[indexFirst];
				second = directions[indexSecond];
			}
			
			double firstAxisValue = RotationUtils.get(axis, corners[indexFirst + 1]);
			double secondAxisValue = RotationUtils.get(axis, corners[indexSecond + 1]);
			
			boolean allInside = true;
			
			for (int j = 0; j < 4; j++) {
				
				Vector2d vector = vectors[j];
				
				double t = (vector.x * second.y - vector.y * second.x) / (first.x * second.y - first.y * second.x);
				if (Double.isNaN(t) || Double.isInfinite(t))
					continue face_loop;
				double s = (vector.y - t * first.y) / second.y;
				if (Double.isNaN(s) || Double.isInfinite(s))
					continue face_loop;
				
				if (t <= 0 || t >= 1 || s <= 0 || s >= 1)
					allInside = false;
				vectorsRelative[j].set(t, s);
			}
			
			if (allInside) {
				for (int j = 0; j < vectorsRelative.length; j++) {
					double distance = calculateDistanceFromPlane(positive, closestValue, vectorsRelative[j], firstAxisValue, secondAxisValue, outerCornerAxis);
					minDistance = Math.min(distance, minDistance);
				}
			} else {
				List<Vector2d> points = IntersectionHelper.getIntersectionShape(vectorsRelative);
				for (int j = 0; j < points.size(); j++) {
					double distance = calculateDistanceFromPlane(positive, closestValue, points.get(j), firstAxisValue, secondAxisValue, outerCornerAxis);
					minDistance = Math.min(distance, minDistance);
				}
			}
			
		}
		
		if (minDistance == Double.MAX_VALUE)
			return -1;
		
		return minDistance;
	}
	
	public static double calculateDistanceFromPlane(boolean positive, double closestValue, Vector2d vec, double firstAxisValue, double secondAxisValue, double outerCornerAxis) {
		double valueAxis = outerCornerAxis + (firstAxisValue - outerCornerAxis) * vec.x + (secondAxisValue - outerCornerAxis) * vec.y;
		return positive ? valueAxis - closestValue : closestValue - valueAxis;
	}
	
	public double calculateOffsetRotated(AxisAlignedBB other, Axis axis, double offset) {
		if (offset == 0)
			return offset;
		
		double distance = calculateDistanceRotated(other, axis, offset);
		
		if (distance < 0 && !equals(distance, 0))
			return offset;
		
		if (offset > 0.0D) {
			if (distance < offset)
				return distance;
			return offset;
		} else if (offset < 0.0D) {
			if (-distance > offset)
				return -distance;
			return offset;
		}
		return offset;
	}
	
	public static final float EPSILON = 0.001F;
	
	public static boolean smallerThanAndEquals(double a, double b) {
		return a < b || equals(a, b);
	}
	
	public static boolean greaterThanAndEquals(double a, double b) {
		return a > b || equals(a, b);
	}
	
	public static boolean equals(double a, double b) {
		return a == b ? true : Math.abs(a - b) < EPSILON;
	}
	
	@Override
	public double calculateYOffsetStepUp(AxisAlignedBB other, AxisAlignedBB otherY, double offset) {
		double newOffset = calculateYOffset(otherY, offset);
		if (offset > 0) {
			if (newOffset < offset)
				return newOffset / 2;
		} else {
			if (newOffset > offset)
				return newOffset / 2;
		}
		
		return newOffset;
	}
	
	public double getMaxTranslated(Axis axis) {
		return getMax(axis) + origin.translationCombined(axis);
	}
	
	public double getMinTranslated(Axis axis) {
		return getMin(axis) + origin.translationCombined(axis);
	}
	
	public double calculateOffset(AxisAlignedBB other, Axis axis, double offset) {
		if (other instanceof OrientatedBoundingBox) {
			if (((OrientatedBoundingBox) other).origin == origin) {
				switch (axis) {
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
		
		if (origin.isRotated())
			return calculateOffsetRotated(other, axis, offset);
		
		Axis one = RotationUtils.getDifferentAxisFirst(axis);
		Axis two = RotationUtils.getDifferentAxisSecond(axis);
		
		if (getMax(other, one) > getMinTranslated(one) && getMin(other, one) < getMaxTranslated(one) && getMax(other, two) > getMinTranslated(two) && getMin(other, two) < getMaxTranslated(two)) {
			if (offset > 0.0D && getMax(other, axis) <= getMinTranslated(axis)) {
				double d1 = getMinTranslated(axis) - getMax(other, axis);
				
				if (d1 < offset) {
					offset = d1;
				}
			} else if (offset < 0.0D && getMin(other, axis) >= getMaxTranslated(axis)) {
				double d0 = getMaxTranslated(axis) - getMin(other, axis);
				
				if (d0 > offset) {
					offset = d0;
				}
			}
			
			return offset;
		}
		
		return offset;
	}
	
	@Override
	public double calculateXOffset(AxisAlignedBB other, double offsetX) {
		return calculateOffset(other, Axis.X, offsetX);
	}
	
	@Override
	public double calculateYOffset(AxisAlignedBB other, double offsetY) {
		return calculateOffset(other, Axis.Y, offsetY);
	}
	
	@Override
	public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
		return calculateOffset(other, Axis.Z, offsetZ);
	}
	
	@Override
	public boolean intersects(AxisAlignedBB other) {
		if (other instanceof OrientatedBoundingBox) {
			if (((OrientatedBoundingBox) other).origin == origin)
				return this.minX < other.maxX && this.maxX > other.minX && this.minY < other.maxY && this.maxY > other.minY && this.minZ < other.maxZ && this.maxZ > other.minZ;
			else {
				OrientatedBoundingBox converted = ((OrientatedBoundingBox) other).origin.getOrientatedBox(origin.getAxisAlignedBox(this));
				return converted.minX < other.maxX && converted.maxX > other.minX && converted.minY < other.maxY && converted.maxY > other.minY && converted.minZ < other.maxZ && converted.maxZ > other.minZ;
			}
		}
		
		return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
	}
	
	@Override
	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		AxisAlignedBB box = origin.getAxisAlignedBox(this);
		return box.minX < maxX && box.maxX > minX && box.minY < maxY && box.maxY > minY && box.minZ < maxZ && box.maxZ > minZ;
	}
	
	@Override
	public String toString() {
		return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
	}
	
	public Vector3d getCenter3d() {
		return new Vector3d(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
	}
	
	public double getPushOutScale(double minScale, OrientatedBoundingBox fakeBox, Vector3d pushVec) {
		double scale = Double.MAX_VALUE;
		
		boolean pushX = pushVec.x != 0;
		boolean pushY = pushVec.y != 0;
		boolean pushZ = pushVec.z != 0;
		
		if (pushX)
			if (pushVec.x > 0)
				scale = Math.min(scale, Math.abs((this.maxX - fakeBox.minX) / pushVec.x));
			else
				scale = Math.min(scale, Math.abs((this.minX - fakeBox.maxX) / pushVec.x));
			
		if (pushY)
			if (pushVec.y > 0)
				scale = Math.min(scale, Math.abs((this.maxY - fakeBox.minY) / pushVec.y));
			else
				scale = Math.min(scale, Math.abs((this.minY - fakeBox.maxY) / pushVec.y));
			
		if (pushZ)
			if (pushVec.z > 0)
				scale = Math.min(scale, Math.abs((this.maxZ - fakeBox.minZ) / pushVec.z));
			else
				scale = Math.min(scale, Math.abs((this.minZ - fakeBox.maxZ) / pushVec.z));
			
		if (scale <= minScale)
			return minScale;
		
		return scale;
		
	}
	
}
