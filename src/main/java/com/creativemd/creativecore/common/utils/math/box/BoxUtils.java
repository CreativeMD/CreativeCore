package com.creativemd.creativecore.common.utils.math.box;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.RotationUtils.BooleanRotation;
import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.collision.CollisionCoordinator;
import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;

public class BoxUtils {
	
	public static boolean equals(double a, double b, double deviation) {
		return a == b ? true : Math.abs(a - b) < deviation;
	}
	
	public static boolean greaterEquals(double a, double b, double deviation) {
		return a >= (b > 0 ? b - deviation : b + deviation);
	}
	
	public static boolean insideRect(double one, double two, double minOne, double minTwo, double maxOne, double maxTwo) {
		return one > minOne && one < maxOne && two > minTwo && two < maxTwo;
	}
	
	public static Vector3d[] getCorners(AxisAlignedBB box) {
		Vector3d[] corners = new Vector3d[BoxCorner.values().length];
		for (int i = 0; i < corners.length; i++) {
			corners[i] = BoxCorner.values()[i].getVector(box);
		}
		return corners;
	}
	
	private static double lengthIgnoreAxis(Vector3d vec, Axis axis) {
		switch (axis) {
		case X:
			return Math.sqrt(vec.y * vec.y + vec.z * vec.z);
		case Y:
			return Math.sqrt(vec.x * vec.x + vec.z * vec.z);
		case Z:
			return Math.sqrt(vec.x * vec.x + vec.y * vec.y);
		default:
			return 0;
		}
	}
	
	private static void includeMaxRotationInBox(Box box, Vector3d vec, Axis axis, CollisionCoordinator coordinator) {
		double rotation = coordinator.getRotationDegree(axis);
		if (rotation == 0)
			return;
		
		Matrix3d matrix = coordinator.getRotationMatrix(axis);
		Double length = null;
		BooleanRotation state = BooleanRotation.getRotationState(axis, vec);
		
		boolean positive = rotation > 0;
		int quarterRotation = 90;
		
		if (rotation >= 90) {
			while (quarterRotation <= Math.abs(rotation) && quarterRotation < 360) {
				EnumFacing facing = positive ? state.clockwiseMaxFacing() : state.counterMaxClockwiseFacing();
				
				if (length == null)
					length = lengthIgnoreAxis(vec, axis);
				
				box.include(facing, length);
				if (coordinator.translation != null)
					box.include(facing, length + VectorUtils.get(facing.getAxis(), coordinator.translation));
				
				state = state.clockwise();
				quarterRotation += 90;
			}
		}
		
		matrix.transform(vec);
		box.include(vec);
		
		if (quarterRotation <= 360 && !state.is(vec)) {
			EnumFacing facing = positive ? state.clockwiseMaxFacing() : state.counterMaxClockwiseFacing();
			
			if (length == null)
				length = lengthIgnoreAxis(vec, axis);
			
			box.include(facing, length);
			if (coordinator.translation != null)
				box.include(facing, length + VectorUtils.get(facing.getAxis(), coordinator.translation));
		}
	}
	
	public static AxisAlignedBB getRotatedSurrounding(AxisAlignedBB boundingBox, CollisionCoordinator coordinator) {
		Vector3d[] corners = getRotatedCorners(boundingBox, coordinator.origin);
		
		Box bb = new Box();
		
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			
			bb.include(vec);
			
			if (coordinator.hasOnlyTranslation()) {
				vec.add(coordinator.translation);
				bb.include(vec);
			} else {
				includeMaxRotationInBox(bb, new Vector3d(vec), Axis.X, coordinator);
				includeMaxRotationInBox(bb, new Vector3d(vec), Axis.Y, coordinator);
				includeMaxRotationInBox(bb, new Vector3d(vec), Axis.Z, coordinator);
				
				coordinator.transform(vec, 1D);
				bb.include(vec);
			}
		}
		
		return bb.getAxisBB();
	}
	
	public static Vector3d[] getRotatedCorners(AxisAlignedBB box, IVecOrigin origin) {
		Vector3d[] corners = getCorners(box);
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			origin.transformPointToWorld(vec);
		}
		return corners;
	}
	
	public static Vector3d[] getOuterCorner(EnumFacing facing, IVecOrigin origin, AxisAlignedBB box, double minOne, double minTwo, double maxOne, double maxTwo) {
		/*Vector3d[] corners = getCorners(box);
		
		boolean positive = facing.getAxisDirection() == AxisDirection.POSITIVE;
		Vector3d corner = null;
		double value = 0;
		BoxCorner selected = null;
		Boolean inside = null;
		Axis axis = facing.getAxis();
		
		Axis one = RotationUtils.getDifferentAxisFirst(axis);
		Axis two = RotationUtils.getDifferentAxisSecond(axis);
		
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			origin.transformPointToWorld(vec);
			
			double vectorValue = RotationUtils.get(axis, vec);
			if (selected == null || (positive ? vectorValue >= value : vectorValue <= value)) {
				if (vectorValue == value) {
					if (inside == null)
						inside = insideRect(RotationUtils.get(one, corner), RotationUtils.get(two, corner), minOne, minTwo, maxOne, maxTwo);
					if (inside)
						continue;
					
					boolean otherInside = insideRect(RotationUtils.get(one, vec), RotationUtils.get(two, vec), minOne, minTwo, maxOne, maxTwo);
					if (otherInside)
						inside = true;
					else
						continue;
				} else
					inside = null;
				corner = vec;
				selected = BoxCorner.values()[i];
				value = vectorValue;
			}
		}
		
		return new Vector3d[] { corners[selected.ordinal()], corners[selected.neighborOne.ordinal()], corners[selected.neighborTwo.ordinal()], corners[selected.neighborThree.ordinal()] };*/
		Vector3d[] corners = getCorners(box);
		
		boolean positive = facing.getAxisDirection() == AxisDirection.POSITIVE;
		double value = 0;
		BoxCorner selected = null;
		Axis axis = facing.getAxis();
		
		Axis one = RotationUtils.getDifferentAxisFirst(axis);
		Axis two = RotationUtils.getDifferentAxisSecond(axis);
		
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			origin.transformPointToWorld(vec);
			
			double vectorValue = VectorUtils.get(axis, vec);
			if (selected == null || (positive ? vectorValue > value : vectorValue < value)) {
				selected = BoxCorner.values()[i];
				value = vectorValue;
			}
		}
		
		return new Vector3d[] { corners[selected.ordinal()], corners[selected.neighborOne.ordinal()], corners[selected.neighborTwo.ordinal()], corners[selected.neighborThree.ordinal()] };
	}
	
	private static class Box {
		
		public double minX;
		public double minY;
		public double minZ;
		public double maxX;
		public double maxY;
		public double maxZ;
		
		public Box() {
			minX = Double.MAX_VALUE;
			minY = Double.MAX_VALUE;
			minZ = Double.MAX_VALUE;
			maxX = -Double.MAX_VALUE;
			maxY = -Double.MAX_VALUE;
			maxZ = -Double.MAX_VALUE;
		}
		
		public void include(Vector3d vec) {
			minX = Math.min(minX, vec.x);
			minY = Math.min(minY, vec.y);
			minZ = Math.min(minZ, vec.z);
			maxX = Math.max(maxX, vec.x);
			maxY = Math.max(maxY, vec.y);
			maxZ = Math.max(maxZ, vec.z);
		}
		
		public void include(EnumFacing facing, double value) {
			switch (facing) {
			case EAST:
				maxX = Math.max(maxX, value);
				break;
			case WEST:
				minX = Math.min(minX, value);
				break;
			case UP:
				maxY = Math.max(maxY, value);
				break;
			case DOWN:
				minY = Math.min(minY, value);
				break;
			case SOUTH:
				maxZ = Math.max(maxZ, value);
				break;
			case NORTH:
				minZ = Math.min(minZ, value);
				break;
			}
		}
		
		public void translate(Vector3d translation) {
			minX += translation.x;
			minY += translation.y;
			minZ += translation.z;
			maxX += translation.x;
			maxY += translation.y;
			maxZ += translation.z;
		}
		
		public AxisAlignedBB getAxisBB() {
			return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		}
		
	}
	
}
