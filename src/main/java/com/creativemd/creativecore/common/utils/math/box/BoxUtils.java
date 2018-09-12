package com.creativemd.creativecore.common.utils.math.box;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.Rotation;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.RotationUtils.BooleanRotation;
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

	private static void includeMaxRotationInBox(Box box, Vector3d vec, Axis axis, Matrix3d matrix, double rotation, Vector3d additionalTranslation) {
		if (matrix == null)
			return;

		Double length = null;

		BooleanRotation state = BooleanRotation.getRotationState(axis, vec);

		if (rotation > 0) {
			double skipRotation = 90;
			while (skipRotation < rotation && skipRotation < 360) {
				EnumFacing facing = state.clockwiseMaxFacing();

				if (length == null)
					length = lengthIgnoreAxis(vec, axis);

				box.include(facing, length);
				if (additionalTranslation != null)
					box.include(facing, length + RotationUtils.get(facing.getAxis(), additionalTranslation));

				state = state.clockwise();
				skipRotation += 90;
			}

			matrix.transform(vec);

			if (skipRotation < 360 && !state.is(vec)) {
				EnumFacing facing = state.clockwiseMaxFacing();

				if (length == null)
					length = lengthIgnoreAxis(vec, axis);

				box.include(facing, length);
				if (additionalTranslation != null)
					box.include(facing, length + RotationUtils.get(facing.getAxis(), additionalTranslation));
			}
		} else {
			double skipRotation = -90;
			while (skipRotation > rotation && skipRotation > -360) {
				EnumFacing facing = state.counterMaxClockwiseFacing();

				if (length == null)
					length = lengthIgnoreAxis(vec, axis);

				box.include(facing, length);
				if (additionalTranslation != null)
					box.include(facing, length + RotationUtils.get(facing.getAxis(), additionalTranslation));

				state = state.counterClockwise();
				skipRotation -= 90;
			}

			matrix.transform(vec);

			if (skipRotation > -360 && !state.is(vec)) {
				EnumFacing facing = state.counterMaxClockwiseFacing();

				if (length == null)
					length = lengthIgnoreAxis(vec, axis);

				box.include(facing, length);
				if (additionalTranslation != null)
					box.include(facing, length + RotationUtils.get(facing.getAxis(), additionalTranslation));
			}
		}
	}

	public static AxisAlignedBB getRotatedSurrounding(AxisAlignedBB box, Vector3d rotationCenter, Matrix3d initRotation, Vector3d initTranslation, Matrix3d addRotX, double rotX, Matrix3d addRotY, double rotY, Matrix3d addRotZ, double rotZ, Vector3d additionalTranslation) {
		Vector3d[] corners = getCorners(box);

		Box bb = new Box();

		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			vec.sub(rotationCenter);

			// Apply initial rotation
			initRotation.transform(vec);
			bb.include(vec);

			// Additional rotation and translation
			includeMaxRotationInBox(bb, vec, Axis.X, addRotX, rotX, additionalTranslation);
			includeMaxRotationInBox(bb, vec, Axis.Y, addRotY, rotY, additionalTranslation);
			includeMaxRotationInBox(bb, vec, Axis.Z, addRotZ, rotZ, additionalTranslation);

			if (additionalTranslation != null)
				vec.add(additionalTranslation);

			bb.include(vec);
		}

		bb.translate(rotationCenter);
		bb.translate(initTranslation);

		return bb.getAxisBB();
	}

	public static Vector3d[] getRotatedCorners(AxisAlignedBB box, Vector3d rotationCenter, Matrix3d rotation, Vector3d translation) {
		Vector3d[] corners = getCorners(box);
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			vec.sub(rotationCenter);
			rotation.transform(vec);
			vec.add(rotationCenter);

			vec.add(translation);
		}
		return corners;
	}

	/*
	 * public static AxisAlignedBB getRotated(AxisAlignedBB box, Vector3d
	 * rotationCenter, Matrix3d rotation, Vector3d translation) { Vector3d[] corners
	 * = getCorners(box);
	 * 
	 * double minX = Double.MAX_VALUE; double minY = Double.MAX_VALUE; double minZ =
	 * Double.MAX_VALUE; double maxX = -Double.MAX_VALUE; double maxY =
	 * -Double.MAX_VALUE; double maxZ = -Double.MAX_VALUE;
	 * 
	 * for (int i = 0; i < corners.length; i++) { Vector3d vec = corners[i];
	 * vec.sub(rotationCenter); rotation.transform(vec); vec.add(rotationCenter);
	 * 
	 * vec.add(translation);
	 * 
	 * minX = Math.min(minX, vec.x); minY = Math.min(minY, vec.y); minZ =
	 * Math.min(minZ, vec.z); maxX = Math.max(maxX, vec.x); maxY = Math.max(maxY,
	 * vec.y); maxZ = Math.max(maxZ, vec.z); }
	 * 
	 * return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ); }
	 */

	public static Vector3d[] getOuterCorner(EnumFacing facing, IVecOrigin origin, AxisAlignedBB box) {
		Vector3d[] corners = getCorners(box);

		boolean positive = facing.getAxisDirection() == AxisDirection.POSITIVE;
		double value = 0;
		BoxCorner selected = null;
		Axis axis = facing.getAxis();

		Matrix3d rotation = origin.rotation();
		Vector3d rotationCenter = origin.center();
		Vector3d translation = origin.translation();

		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			vec.sub(rotationCenter);
			rotation.transform(vec);
			vec.add(rotationCenter);

			vec.add(translation);

			double vectorValue = RotationUtils.get(axis, vec);
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

	public static enum BoxCorner {
		EUN(EnumFacing.EAST, EnumFacing.UP, EnumFacing.NORTH),
		EUS(EnumFacing.EAST, EnumFacing.UP, EnumFacing.SOUTH),
		EDN(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.NORTH),
		EDS(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.SOUTH),
		WUN(EnumFacing.WEST, EnumFacing.UP, EnumFacing.NORTH),
		WUS(EnumFacing.WEST, EnumFacing.UP, EnumFacing.SOUTH),
		WDN(EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.NORTH),
		WDS(EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.SOUTH);

		public final EnumFacing x;
		public final EnumFacing y;
		public final EnumFacing z;

		public BoxCorner neighborOne;
		public BoxCorner neighborTwo;
		public BoxCorner neighborThree;

		private BoxCorner(EnumFacing x, EnumFacing y, EnumFacing z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		private void initCorner() {
			neighborOne = getCorner(x.getOpposite(), y, z);
			neighborTwo = getCorner(x, y.getOpposite(), z);
			neighborThree = getCorner(x, y, z.getOpposite());
		}

		public Vector3d getVector(AxisAlignedBB box) {
			return new Vector3d(CreativeAxisAlignedBB.getCornerX(box, this), CreativeAxisAlignedBB.getCornerY(box, this), CreativeAxisAlignedBB.getCornerZ(box, this));
		}

		public boolean isFacingPositive(Axis axis) {
			return getFacing(axis).getAxisDirection() == AxisDirection.POSITIVE;
		}

		public EnumFacing getFacing(Axis axis) {
			switch (axis) {
			case X:
				return x;
			case Y:
				return y;
			case Z:
				return z;
			}
			return null;
		}

		public BoxCorner flip(Axis axis) {
			switch (axis) {
			case X:
				return getCorner(x.getOpposite(), y, z);
			case Y:
				return getCorner(x, y.getOpposite(), z);
			case Z:
				return getCorner(x, y, z.getOpposite());
			}
			return null;
		}

		public BoxCorner rotate(Rotation rotation) {
			int normalX = x.getAxisDirection().getOffset();
			int normalY = y.getAxisDirection().getOffset();
			int normalZ = z.getAxisDirection().getOffset();
			return getCorner(EnumFacing.getFacingFromAxis(rotation.getMatrix().getX(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.X), EnumFacing.getFacingFromAxis(rotation.getMatrix().getY(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.Y), EnumFacing.getFacingFromAxis(rotation.getMatrix().getZ(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.Z));
		}

		public static BoxCorner getCornerUnsorted(EnumFacing facing) {
			switch (facing.getAxis()) {
			case X:
				return getCorner(facing, EnumFacing.UP, EnumFacing.SOUTH);
			case Y:
				return getCorner(EnumFacing.EAST, facing, EnumFacing.SOUTH);
			case Z:
				return getCorner(EnumFacing.EAST, EnumFacing.UP, facing);
			}
			return null;
		}

		public static BoxCorner getCornerUnsorted(EnumFacing facing, EnumFacing facing2, EnumFacing facing3) {
			return getCorner(facing.getAxis() != Axis.X ? facing2.getAxis() != Axis.X ? facing3 : facing2 : facing, facing.getAxis() != Axis.Y ? facing2.getAxis() != Axis.Y ? facing3 : facing2 : facing, facing.getAxis() != Axis.Z ? facing2.getAxis() != Axis.Z ? facing3 : facing2 : facing);
		}

		public static BoxCorner getCorner(EnumFacing x, EnumFacing y, EnumFacing z) {
			for (BoxCorner corner : BoxCorner.values()) {
				if (corner.x == x && corner.y == y && corner.z == z)
					return corner;
			}
			return null;
		}

		private static void initCorners() {
			for (BoxCorner corner : BoxCorner.values()) {
				corner.initCorner();
			}
		}

	}

	public static enum BoxFace {
		EAST(EnumFacing.EAST, new BoxCorner[] { BoxCorner.EUS, BoxCorner.EDS, BoxCorner.EDN, BoxCorner.EUN }),
		WEST(EnumFacing.WEST, new BoxCorner[] { BoxCorner.WUN, BoxCorner.WDN, BoxCorner.WDS, BoxCorner.WUS }),
		UP(EnumFacing.UP, new BoxCorner[] { BoxCorner.WUN, BoxCorner.WUS, BoxCorner.EUS, BoxCorner.EUN }),
		DOWN(EnumFacing.DOWN, new BoxCorner[] { BoxCorner.WDS, BoxCorner.WDN, BoxCorner.EDN, BoxCorner.EDS }),
		SOUTH(EnumFacing.SOUTH, new BoxCorner[] { BoxCorner.WUS, BoxCorner.WDS, BoxCorner.EDS, BoxCorner.EUS }),
		NORTH(EnumFacing.NORTH, new BoxCorner[] { BoxCorner.EUN, BoxCorner.EDN, BoxCorner.WDN, BoxCorner.WUN });

		public final EnumFacing facing;
		public final BoxCorner[] corners;

		BoxFace(EnumFacing facing, BoxCorner[] corners) {
			this.facing = facing;
			this.corners = corners;
		}

		public Vector3d first(Vector3d[] corners) {
			return corners[this.corners[0].ordinal()];
		}

		public Vector3d normal(Vector3d[] corners) {
			Vector3d origin = first(corners);
			Vector3d first = new Vector3d(corners[this.corners[1].ordinal()]);
			Vector3d second = new Vector3d(corners[this.corners[2].ordinal()]);
			first.sub(origin);
			second.sub(origin);
			return new Vector3d(first.y * second.z - first.z * second.y, first.z * second.x - first.x * second.z, first.x * second.y - first.y * second.x);
		}

		public static BoxFace getFace(EnumFacing facing) {
			switch (facing) {
			case EAST:
				return EAST;
			case WEST:
				return WEST;
			case UP:
				return UP;
			case DOWN:
				return DOWN;
			case SOUTH:
				return SOUTH;
			case NORTH:
				return NORTH;
			default:
				return null;
			}
		}

		public static BoxFace getFace(Axis axis, boolean direction) {
			switch (axis) {
			case X:
				return direction ? EAST : WEST;
			case Y:
				return direction ? UP : DOWN;
			case Z:
				return direction ? SOUTH : NORTH;
			default:
				return null;
			}
		}
	}

	static {
		BoxCorner.initCorners();
	}
}
