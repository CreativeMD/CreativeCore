package com.creativemd.creativecore.common.utils.math.collision;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.BooleanUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils.BoxCorner;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils.BoxFace;
import com.creativemd.creativecore.common.utils.math.box.CreativeAxisAlignedBB;
import com.creativemd.creativecore.common.utils.math.box.OrientatedBoundingBox;

import net.minecraft.util.EnumFacing;
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
	
	public Double binarySearch(@Nullable Double value, AxisAlignedBB toCheck, double checkRadiusSquared, Vector3d center, CollisionCoordinator coordinator) {
		if (coordinator.isSimple) {
			Double t = searchBetweenSimple(value, center, new Vector3d(center), new Vector3d(), 0, 1, coordinator, 0);
			if (t != null && intersects(toCheck, checkRadiusSquared, center, t, coordinator))
				return t;
			
			return null;
		} else if (coordinator.hasOneRotation && !coordinator.hasTranslation) {
			int halfRotations = coordinator.getNumberOfHalfRotations();
			double halfRotationSize = 1D / halfRotations;
			
			Vector3d temp = new Vector3d();
			Vector3d start = new Vector3d(center);
			
			Double t = searchBetweenSimple(value, center, start, temp, 0, halfRotationSize, coordinator, 0);
			if (t != null && intersects(toCheck, checkRadiusSquared, center, t, coordinator))
				return t;
			
			start.set(center);
			coordinator.transformInverted(start, halfRotationSize);
			t = searchBetweenSimple(value, center, new Vector3d(center), temp, halfRotationSize, halfRotationSize * 2, coordinator, 0);
			if (t != null && intersects(toCheck, checkRadiusSquared, center, t, coordinator))
				return t;
			
			return null;
		}
		
		// Advanced!!!!!!! At this point there is no way to figure out how the matrix
		// behaves, so just scan somewhere with the given resolution and hope to find
		// the earliest hit
		Vector3d start = new Vector3d(center);
		Vector3d temp = new Vector3d();
		int halfRotations = coordinator.getNumberOfHalfRotations();
		double halfRotationSize = 1D / halfRotations;
		for (int i = 0; i < halfRotations; i++) {
			double startT = halfRotationSize * i;
			double endT = halfRotationSize * (i + 1);
			
			if (startT != 0) {
				start.set(center);
				coordinator.transformInverted(start, startT);
			}
			
			if (value != null && value <= startT)
				return null;
			
			Double t = searchBetweenSimple(value, center, start, temp, startT, endT, coordinator, 0);
			if (t != null && intersects(toCheck, checkRadiusSquared, center, t, coordinator))
				return t;
		}
		
		return null;
	}
	
	protected Double searchBetweenSimple(@Nullable Double value, Vector3d center, Vector3d start, Vector3d temp, double startT, double endT, CollisionCoordinator coordinator, int steps) {
		if (value != null && value < startT)
			return null;
		
		Boolean beforeFront = isInFront(start);
		if (beforeFront == null)
			return startT;
		
		temp.set(center);
		coordinator.transformInverted(temp, endT);
		Boolean afterFront = isInFront(temp);
		if (afterFront == null)
			return Math.min(value, endT);
		
		if (beforeFront != afterFront) {
			if (steps < accuracySteps) {
				steps++;
				double halfT = (startT + endT) / 2D;
				
				temp.set(center);
				coordinator.transformInverted(temp, halfT);
				
				Boolean halfFront = isInFront(temp);
				if (halfFront == null)
					return Math.min(value, halfT);
				
				if (beforeFront != halfFront)
					return searchBetweenSimple(value, center, start, temp, startT, halfT, coordinator, steps);
				return searchBetweenSimple(value, center, temp, start, halfT, endT, coordinator, steps);
			}
			return startT;
		}
		return null;
	}
	
	public boolean intersects(AxisAlignedBB toCheck, double checkRadiusSquared, Vector3d center, double t, CollisionCoordinator coordinator) {
		Vector3d cachedCenter = new Vector3d(cache.center);
		coordinator.origin.transformPointToWorld(cachedCenter);
		coordinator.transform(cachedCenter, t);
		cachedCenter.sub(center);
		
		if (cachedCenter.lengthSquared() >= checkRadiusSquared + cache.radiusSquared)
			return false;
		
		Matrix4d matrix = coordinator.getInverted(t);
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (int i = 0; i < BoxCorner.values().length; i++) {
			Vector3d corner = BoxCorner.values()[i].getVector(toCheck);
			
			coordinator.transform(matrix, corner);
			coordinator.origin.transformPointToFakeWorld(corner);
			
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
	
	public static CollidingPlane[] getPlanes(CreativeAxisAlignedBB box, PlaneCache cache, CollisionCoordinator coordinator) {
		Vector3d[] corners = BoxUtils.getRotatedCorners(box, coordinator.origin);
		
		boolean east = coordinator.offX > 0;
		boolean west = coordinator.offY < 0;
		boolean up = coordinator.offY > 0;
		boolean down = coordinator.offY < 0;
		boolean south = coordinator.offZ > 0;
		boolean north = coordinator.offZ < 0;
		
		if (coordinator.hasRotY || coordinator.hasRotZ)
			east = west = true;
		
		if (coordinator.hasRotX || coordinator.hasRotZ)
			up = down = true;
		
		if (coordinator.hasRotX || coordinator.hasRotY)
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
	
	public static EnumFacing getDirection(CollisionCoordinator coordinator, OrientatedBoundingBox box, Vector3d center) {
		double x = (center.x - box.cache.center.x) / (box.maxX - box.minX);
		double y = (center.y - box.cache.center.y) / (box.maxY - box.minY);
		double z = (center.z - box.cache.center.z) / (box.maxZ - box.minZ);
		
		boolean xy = Math.abs(x) > Math.abs(y);
		boolean xz = Math.abs(x) > Math.abs(z);
		boolean yz = Math.abs(y) > Math.abs(z);
		if (xy && xz)
			return x > 0 ? EnumFacing.EAST : EnumFacing.WEST;
		else if (!xz && !yz)
			return z > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
		return y > 0 ? EnumFacing.UP : EnumFacing.DOWN;
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
		public EnumFacing facing;
		public OrientatedBoundingBox pushBox;
		
		public AxisAlignedBB entityBox;
		public OrientatedBoundingBox entityBoxOrientated;
	}
}
