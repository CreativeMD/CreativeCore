package com.creativemd.creativecore.common.utils.math.box;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.BooleanUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils.BoxCorner;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils.BoxFace;
import com.creativemd.creativecore.common.utils.math.vec.MatrixUtils.MatrixLookupTable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;

public class CollidingPlane {
	
	public final CreativeAxisAlignedBB bb;
	public final EnumFacing facing;
	public final PlaneCache cache;
	protected final Vector3d origin;
	protected final Vector3d normal;
	
	public CollidingPlane(CreativeAxisAlignedBB bb, EnumFacing facing, PlaneCache cache, Vector3d[] corners, BoxCorner[] planeCorners) {
		this.bb = bb;
		this.facing = facing;
		this.cache = cache;
		this.origin = corners[planeCorners[0].ordinal()];
		Vector3d first = corners[planeCorners[1].ordinal()];
		Vector3d second = corners[planeCorners[2].ordinal()];
		
		this.normal = new Vector3d((first.y - origin.y) * (second.z - origin.z) - (first.z - origin.z) * (second.y - origin.y), (first.z - origin.z) * (second.x - origin.x) - (first.x - origin.x) * (second.z - origin.z), (first.x - origin.x) * (second.y - origin.y) - (first.y - origin.y) * (second.x - origin.x));
	}
	
	public Boolean isInFront(Vector3d vec) {
		double scalar = (vec.x - origin.x) * normal.x + (vec.y - origin.y) * normal.y + (vec.z - origin.z) * normal.z;
		if (scalar > 0)
			return true;
		else if (scalar < 0)
			return false;
		return null;
	}
	
	public static final int accuracySteps = 10;
	
	public Double binarySearch(@Nullable Double value, AxisAlignedBB toCheck, double checkRadiusSquared, Vector3d center, MatrixLookupTable table) {
		if (table.isSimple) {
			Double t = searchBetweenSimple(value, toCheck, center, new Vector3d(center), new Vector3d(), 0, 1, table, 0);
			if (t != null && intersects(toCheck, checkRadiusSquared, center, t, table))
				return t;
			
			return null;
		} else if (table.hasOneRotation && !table.hasTranslation) {
			int halfRotations = table.getNumberOfHalfRotations();
			double halfRotationSize = 1D / halfRotations;
			
			Vector3d temp = new Vector3d();
			Vector3d start = new Vector3d(center);
			
			Double t = searchBetweenSimple(value, toCheck, center, start, temp, 0, halfRotationSize, table, 0);
			if (t != null && intersects(toCheck, checkRadiusSquared, center, t, table))
				return t;
			
			start.set(center);
			table.transformInverted(start, halfRotationSize);
			t = searchBetweenSimple(value, toCheck, center, new Vector3d(center), temp, halfRotationSize, halfRotationSize * 2, table, 0);
			if (t != null && intersects(toCheck, checkRadiusSquared, center, t, table))
				return t;
			
			return null;
		}
		
		// Advanced!!!!!!! At this point there is no way to figure out how the matrix behaves, so just scan somewhere with the given resolution and hope to find the earliest hit
		Vector3d start = new Vector3d(center);
		Vector3d temp = new Vector3d();
		int halfRotations = table.getNumberOfHalfRotations();
		double halfRotationSize = 1D / halfRotations;
		for (int i = 0; i < halfRotations; i++) {
			double startT = halfRotationSize * i;
			double endT = halfRotationSize * (i + 1);
			
			if (startT != 0) {
				start.set(center);
				table.transformInverted(start, startT);
			}
			
			if (value <= startT)
				return null;
			
			Double t = searchBetweenSimple(value, toCheck, center, start, temp, startT, endT, table, 0);
			if (t != null && intersects(toCheck, checkRadiusSquared, center, t, table))
				return t;
		}
		
		return null;
	}
	
	protected Double searchBetweenSimple(@Nullable Double value, AxisAlignedBB toCheck, Vector3d center, Vector3d start, Vector3d temp, double startT, double endT, MatrixLookupTable table, int steps) {
		if (value != null && value < startT)
			return null;
		
		Boolean beforeFront = isInFront(start);
		if (beforeFront == null)
			return startT;
		
		temp.set(center);
		table.transformInverted(temp, endT);
		Boolean afterFront = isInFront(temp);
		if (afterFront == null)
			return endT;
		
		if (beforeFront != afterFront) {
			if (steps < accuracySteps) {
				steps++;
				double halfT = (startT + endT) / 2D;
				
				temp.set(center);
				table.transformInverted(temp, halfT);
				
				Boolean halfFront = isInFront(temp);
				if (halfFront == null)
					return halfT;
				
				if (beforeFront != halfFront)
					return searchBetweenSimple(value, toCheck, center, start, temp, startT, halfT, table, steps);
				return searchBetweenSimple(value, toCheck, center, temp, start, halfT, endT, table, steps);
			}
			return startT;
		}
		return null;
	}
	
	public boolean intersects(AxisAlignedBB toCheck, double checkRadiusSquared, Vector3d center, double t, MatrixLookupTable table) {
		if (bb.contains(center))
			return true;
		
		Vector3d temp = new Vector3d(center);
		temp.sub(cache.center);
		if (temp.lengthSquared() >= checkRadiusSquared + cache.radiusSquared)
			return false;
		
		Matrix4d matrix = table.getInverted(t);
		
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (int i = 0; i < BoxCorner.values().length; i++) {
			Vector3d corner = BoxCorner.values()[i].getVector(toCheck);
			
			corner.sub(table.origin.translation());
			corner.sub(table.rotationCenter);
			table.origin.rotationInv().transform(corner);
			corner.add(table.rotationCenter);
			
			table.transform(matrix, table.rotationCenter, corner);
			
			if (bb.contains(corner))
				return true;
			
			minX = Math.min(minX, corner.x);
			minY = Math.min(minY, corner.y);
			minZ = Math.min(minZ, corner.z);
			maxX = Math.max(maxX, corner.x);
			maxY = Math.max(maxY, corner.y);
			maxZ = Math.max(maxZ, corner.z);
		}
		
		return bb.minX < maxX && bb.maxX > minX && bb.minY < maxY && bb.maxY > minY && bb.minZ < maxZ && bb.maxZ > minZ;
	}
	
	public static CollidingPlane[] getPlanes(CreativeAxisAlignedBB box, PlaneCache cache, MatrixLookupTable table) {
		Vector3d[] corners = BoxUtils.getCorners(box);
		
		boolean east = table.x > 0;
		boolean west = table.x < 0;
		boolean up = table.y > 0;
		boolean down = table.y < 0;
		boolean south = table.z > 0;
		boolean north = table.z < 0;
		
		if (table.hasRotY || table.hasRotZ)
			east = west = true;
		
		if (table.hasRotX || table.hasRotZ)
			up = down = true;
		
		if (table.hasRotX || table.hasRotY)
			south = north = true;
		
		CollidingPlane[] planes = new CollidingPlane[BooleanUtils.countTrue(east, west, up, down, south, north)];
		int index = 0;
		if (east) {
			planes[index] = new CollidingPlane(box, EnumFacing.EAST, cache, corners, BoxFace.getFace(EnumFacing.EAST).corners);
			index++;
		}
		if (west) {
			planes[index] = new CollidingPlane(box, EnumFacing.WEST, cache, corners, BoxFace.getFace(EnumFacing.WEST).corners);
			index++;
		}
		if (up) {
			planes[index] = new CollidingPlane(box, EnumFacing.UP, cache, corners, BoxFace.getFace(EnumFacing.UP).corners);
			index++;
		}
		if (down) {
			planes[index] = new CollidingPlane(box, EnumFacing.DOWN, cache, corners, BoxFace.getFace(EnumFacing.DOWN).corners);
			index++;
		}
		if (south) {
			planes[index] = new CollidingPlane(box, EnumFacing.SOUTH, cache, corners, BoxFace.getFace(EnumFacing.SOUTH).corners);
			index++;
		}
		if (north) {
			planes[index] = new CollidingPlane(box, EnumFacing.NORTH, cache, corners, BoxFace.getFace(EnumFacing.NORTH).corners);
			index++;
		}
		
		return planes;
	}
	
	public static EnumFacing getDirection(OrientatedBoundingBox box, CollidingPlane[] planes, Vector3d center) {
		if (box.contains(center))
			return null;
		
		Boolean positiveX = null;
		Boolean positiveY = null;
		Boolean positiveZ = null;
		
		for (CollidingPlane plane : planes) {
			Boolean result = plane.isInFront(center);
			if (result == null || result)
				switch (plane.facing.getAxis()) {
				case X:
					positiveX = plane.facing.getAxisDirection() == AxisDirection.POSITIVE;
					break;
				case Y:
					positiveY = plane.facing.getAxisDirection() == AxisDirection.POSITIVE;
					break;
				case Z:
					positiveZ = plane.facing.getAxisDirection() == AxisDirection.POSITIVE;
					break;
				default:
					throw new InternalError("1 + 1 = 3");
				}
		}
		
		if (positiveX == null && positiveY == null && positiveZ == null)
			return null;
		
		if (positiveY == null && positiveZ == null)
			return positiveX == null ? null : (positiveX ? EnumFacing.EAST : EnumFacing.WEST);
		if (positiveX == null && positiveZ == null)
			return positiveY == null ? null : (positiveY ? EnumFacing.UP : EnumFacing.DOWN);
		if (positiveX == null && positiveY == null)
			return positiveZ == null ? null : (positiveZ ? EnumFacing.SOUTH : EnumFacing.NORTH);
		
		Vector3d relative = new Vector3d(center);
		relative.sub(box.cache.center);
		
		Vector3d size = box.getSize3d();
		size.normalize();
		relative.x *= size.x;
		relative.y *= size.y;
		relative.z *= size.z;
		
		if (positiveX != null && positiveY != null && positiveZ != null) {
			if (Math.abs(relative.x) > Math.abs(relative.y))
				if (Math.abs(relative.x) > Math.abs(relative.z))
					return positiveX ? EnumFacing.EAST : EnumFacing.WEST;
				else
					return positiveZ ? EnumFacing.SOUTH : EnumFacing.NORTH;
			else if (Math.abs(relative.y) > Math.abs(relative.z))
				return positiveY ? EnumFacing.UP : EnumFacing.DOWN;
			else
				return positiveZ ? EnumFacing.SOUTH : EnumFacing.NORTH;
		}
		
		if (positiveX != null && positiveY != null)
			if (Math.abs(relative.x) > Math.abs(relative.y))
				return positiveX ? EnumFacing.EAST : EnumFacing.WEST;
			else
				return positiveY ? EnumFacing.UP : EnumFacing.DOWN;
			
		if (positiveY != null && positiveZ != null)
			if (Math.abs(relative.y) > Math.abs(relative.z))
				return positiveY ? EnumFacing.UP : EnumFacing.DOWN;
			else
				return positiveZ ? EnumFacing.SOUTH : EnumFacing.NORTH;
			
		if (positiveX != null && positiveZ != null)
			if (Math.abs(relative.x) > Math.abs(relative.z))
				return positiveX ? EnumFacing.EAST : EnumFacing.WEST;
			else
				return positiveZ ? EnumFacing.SOUTH : EnumFacing.NORTH;
			
		throw new InternalError("Math has failed: 1 != 1");
	}
	
	public static class PlaneCache {
		
		public CollidingPlane[] planes;
		public final Vector3d center;
		public final double radiusSquared;
		
		public PlaneCache(AxisAlignedBB box) {
			this.radiusSquared = (box.minX * box.maxX + box.minY * box.maxY + box.minZ * box.maxZ) * 0.5;
			this.center = new Vector3d(box.minX + (box.maxX - box.minX) * 0.5D, box.minY + (box.maxY - box.minY) * 0.5D, box.minZ + (box.maxZ - box.minZ) * 0.5D);
		}
		
		public boolean isCached() {
			return planes != null;
		}
		
		public void reset() {
			planes = null;
		}
		
	}
	
	public static class PushCache {
		
		public OrientatedBoundingBox pushBox;
		public EnumFacing facing;
		
		public AxisAlignedBB entityBox;
		
	}
}
