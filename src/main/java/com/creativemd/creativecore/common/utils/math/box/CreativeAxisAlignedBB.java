package com.creativemd.creativecore.common.utils.math.box;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.box.BoxUtils.BoxCorner;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CreativeAxisAlignedBB extends AxisAlignedBB {

	public CreativeAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
		super(x1, y1, z1, x2, y2, z2);
	}

	public CreativeAxisAlignedBB(BlockPos pos) {
		super(pos);
	}

	public CreativeAxisAlignedBB(BlockPos pos1, BlockPos pos2) {
		super(pos1, pos2);
	}

	public boolean contains(Vector3d vec) {
		if (vec.x > this.minX && vec.x < this.maxX) {
			if (vec.y > this.minY && vec.y < this.maxY) {
				return vec.z > this.minZ && vec.z < this.maxZ;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public double calculateYOffsetStepUp(AxisAlignedBB other, AxisAlignedBB otherY, double offset) {
		return calculateYOffset(other, offset);
	}

	protected double getValueOfFacing(EnumFacing facing) {
		switch (facing) {
		case EAST:
			return maxX;
		case WEST:
			return minX;
		case UP:
			return maxY;
		case DOWN:
			return minY;
		case SOUTH:
			return maxZ;
		case NORTH:
			return minZ;

		}
		return 0;
	}

	public Vec3d getCorner(BoxCorner corner) {
		return new Vec3d(getCornerX(corner), getCornerY(corner), getCornerZ(corner));
	}

	public Vector3d getCornerVector3d(BoxCorner corner) {
		return new Vector3d(getCornerX(corner), getCornerY(corner), getCornerZ(corner));
	}

	public double getCornerValue(BoxCorner corner, Axis axis) {
		return getValueOfFacing(corner.getFacing(axis));
	}

	public double getCornerX(BoxCorner corner) {
		return getValueOfFacing(corner.x);
	}

	public double getCornerY(BoxCorner corner) {
		return getValueOfFacing(corner.y);
	}

	public double getCornerZ(BoxCorner corner) {
		return getValueOfFacing(corner.z);
	}

	public Vec3d getSize() {
		return new Vec3d(maxX - minX, maxY - minY, maxZ - minZ);
	}

	public Vector3d getSize3d() {
		return new Vector3d(maxX - minX, maxY - minY, maxZ - minZ);
	}

	public double getVolume() {
		return (maxX - minX) * (maxY - minY) * (maxZ - minZ);
	}

	public double getIntersectionVolume(AxisAlignedBB other) {
		double d0 = Math.max(this.minX, other.minX);
		double d1 = Math.max(this.minY, other.minY);
		double d2 = Math.max(this.minZ, other.minZ);
		double d3 = Math.min(this.maxX, other.maxX);
		double d4 = Math.min(this.maxY, other.maxY);
		double d5 = Math.min(this.maxZ, other.maxZ);
		if (d0 < d3 && d1 < d4 && d2 < d5)
			return (d3 - d0) * (d4 - d1) * (d5 - d2);
		return 0;
	}

	public double getSize(Axis axis) {
		switch (axis) {
		case X:
			return maxX - minX;
		case Y:
			return maxY - minY;
		case Z:
			return maxZ - minZ;
		}
		return 0;
	}

	public double getMin(Axis axis) {
		switch (axis) {
		case X:
			return minX;
		case Y:
			return minY;
		case Z:
			return minZ;
		}
		return 0;
	}

	public double getMax(Axis axis) {
		switch (axis) {
		case X:
			return maxX;
		case Y:
			return maxY;
		case Z:
			return maxZ;
		}
		return 0;
	}

	public static double getValueOfFacing(AxisAlignedBB bb, EnumFacing facing) {
		switch (facing) {
		case EAST:
			return bb.maxX;
		case WEST:
			return bb.minX;
		case UP:
			return bb.maxY;
		case DOWN:
			return bb.minY;
		case SOUTH:
			return bb.maxZ;
		case NORTH:
			return bb.minZ;

		}
		return 0;
	}

	public static double getMin(AxisAlignedBB bb, Axis axis) {
		switch (axis) {
		case X:
			return bb.minX;
		case Y:
			return bb.minY;
		case Z:
			return bb.minZ;
		}
		return 0;
	}

	public static double getMax(AxisAlignedBB bb, Axis axis) {
		switch (axis) {
		case X:
			return bb.maxX;
		case Y:
			return bb.maxY;
		case Z:
			return bb.maxZ;
		}
		return 0;
	}

	public static Vec3d getCorner(AxisAlignedBB bb, BoxCorner corner) {
		return new Vec3d(getCornerX(bb, corner), getCornerY(bb, corner), getCornerZ(bb, corner));
	}

	public static double getCornerValue(AxisAlignedBB bb, BoxCorner corner, Axis axis) {
		return getValueOfFacing(bb, corner.getFacing(axis));
	}

	public static double getCornerX(AxisAlignedBB bb, BoxCorner corner) {
		return getValueOfFacing(bb, corner.x);
	}

	public static double getCornerY(AxisAlignedBB bb, BoxCorner corner) {
		return getValueOfFacing(bb, corner.y);
	}

	public static double getCornerZ(AxisAlignedBB bb, BoxCorner corner) {
		return getValueOfFacing(bb, corner.z);
	}

	public static boolean isClosest(Vec3d p_186661_1_, @Nullable Vec3d p_186661_2_, Vec3d p_186661_3_) {
		return p_186661_2_ == null || p_186661_1_.squareDistanceTo(p_186661_3_) < p_186661_1_.squareDistanceTo(p_186661_2_);
	}
}
