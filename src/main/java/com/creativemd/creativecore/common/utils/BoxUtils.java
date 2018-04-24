package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.collision.CreativeAxisAlignedBB;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;

public class BoxUtils {
	
	public static Matrix3d createIdentityMatrix()
	{
		Matrix3d matrix = new Matrix3d();
		matrix.setIdentity();
		return matrix;
	}
	
	public static Matrix3d createRotationMatrixX(double angle)
	{
		Matrix3d matrix = new Matrix3d();
		matrix.rotX(Math.toRadians(angle));
		return matrix;
	}
	
	public static Matrix3d createRotationMatrixY(double angle)
	{
		Matrix3d matrix = new Matrix3d();
		matrix.rotY(Math.toRadians(angle));
		return matrix;
	}
	
	public static Matrix3d createRotationMatrixZ(double angle)
	{
		Matrix3d matrix = new Matrix3d();
		matrix.rotZ(Math.toRadians(angle));
		return matrix;
	}
	
	public static boolean equals(double a, double b, double deviation)
	{
	    return a == b ? true : Math.abs(a - b) < deviation;
	}
	
	public static boolean greaterEquals(double a, double b, double deviation)
	{
		return a >= (b > 0 ? b - deviation : b + deviation);
	}
	
	
	/**
	 * It is used to improve performance
	 * @param will contain the new compressed boxes
	 * @param deviation if zero this will be 100% accurate, otherwise it will try to compromise the boxes
	 */
	public static void compressBoxes(ArrayList<AxisAlignedBB> boxes, double deviation)
	{
		int size = 0;
		while(size != boxes.size())
		{
			size = boxes.size();
			int i = 0;
			while(i < boxes.size()){
				int j = 0;
				while(j < boxes.size()) {
					if(i != j)
					{
						AxisAlignedBB box = combineBoxes(boxes.get(i), boxes.get(j), deviation);
						if(box != null)
						{
							boxes.set(i, box);
							boxes.remove(j);
							if(i > j)
								i--;
							continue;
						}
					}
					j++;
				}
				i++;
			}
		}
	}
	
	public static AxisAlignedBB sumBox(AxisAlignedBB box1, AxisAlignedBB box2)
	{
		return new AxisAlignedBB(Math.min(box1.minX, box2.minX), Math.min(box1.minY, box2.minY), Math.min(box1.minZ, box2.minZ), Math.max(box1.maxX, box2.maxX), Math.max(box1.maxY, box2.maxY), Math.max(box1.maxZ, box2.maxZ));
	}
	
	public static AxisAlignedBB combineBoxes(AxisAlignedBB box1, AxisAlignedBB box2, double deviation)
	{
		if(deviation == 0)
		{
			boolean x = box1.minX == box2.minX && box1.maxX == box2.maxX;
			boolean y = box1.minY == box2.minY && box1.maxY == box2.maxY;
			boolean z = box1.minZ == box2.minZ && box1.maxZ == box2.maxZ;
			
			if(x && y && z)
			{
				return box1;
			}
			if(x && y)
			{
				if(box1.maxZ >= box2.minZ && box1.minZ <= box2.maxZ)
					return new AxisAlignedBB(box1.minX, box1.minY, Math.min(box1.minZ, box2.minZ), box1.maxX, box1.maxY, Math.max(box1.maxZ, box2.maxZ));
			}
			if(x && z)
			{
				if(box1.maxY >= box2.minY && box1.minY <= box2.maxY)
					return new AxisAlignedBB(box1.minX, Math.min(box1.minY, box2.minY), box1.minZ, box1.maxX, Math.max(box1.maxY, box2.maxY), box1.maxZ);
			}
			if(y && z)
			{
				if(box1.maxX >= box2.minX && box1.minX <= box2.maxX)
					return new AxisAlignedBB(Math.min(box1.minX, box2.minX), box1.minY, box1.minZ, Math.max(box1.maxX, box2.maxX), box1.maxY, box1.maxZ);
			}
			return null;
		}else{
			boolean x = equals(box1.minX, box2.minX, deviation) && equals(box1.maxX, box2.maxX, deviation);
			boolean y = equals(box1.minY, box2.minY, deviation) && equals(box1.maxY, box2.maxY, deviation);
			boolean z = equals(box1.minZ, box2.minZ, deviation) && equals(box1.maxZ, box2.maxZ, deviation);
			
			if(x && y && z)
				return sumBox(box1, box2);
			
			if(x && y && greaterEquals(box1.maxZ, box2.minZ, deviation) && greaterEquals(box2.maxZ, box1.minZ, deviation))
				return sumBox(box1, box2);
			
			if(x && z && greaterEquals(box1.maxY, box2.minY, deviation) && greaterEquals(box2.maxY, box1.minY, deviation))
				return sumBox(box1, box2);
			
			if(y && z && greaterEquals(box1.maxX, box2.minX, deviation) && greaterEquals(box2.maxX, box1.minX, deviation))
				return sumBox(box1, box2);
			
			return null;
		}
	}
	
	public static Vector3d[] getCorners(AxisAlignedBB box)
	{
		Vector3d[] corners = new Vector3d[BoxCorner.values().length];
		for (int i = 0; i < corners.length; i++) {
			corners[i] = BoxCorner.values()[i].getVector(box);
		}
		return corners;
	}
	
	public static AxisAlignedBB getRotated(AxisAlignedBB box, Vector3d rotationCenter, Matrix3d rotationX, Matrix3d rotationY, Matrix3d rotationZ, Vector3d translation)
	{
		Vector3d[] corners = getCorners(box);
		
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		Matrix3d newMatrix = new Matrix3d(rotationZ);
		newMatrix.mul(rotationY);
		newMatrix.mul(rotationX);
		
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			vec.sub(rotationCenter);
			newMatrix.transform(vec);
			vec.add(rotationCenter);
			
			vec.add(translation);
			
			minX = Math.min(minX, vec.x);
			minY = Math.min(minY, vec.y);
			minZ = Math.min(minZ, vec.z);
			maxX = Math.max(maxX, vec.x);
			maxY = Math.max(maxY, vec.y);
			maxZ = Math.max(maxZ, vec.z);
		}
		
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public static Vector3d[] getOuterCorner(AxisAlignedBB other, EnumFacing facing, IVecOrigin origin, AxisAlignedBB box)
	{
		Vector3d[] corners = getCorners(box);
		
		boolean positive = facing.getAxisDirection() == AxisDirection.POSITIVE;
		double value = 0;
		BoxCorner selected = null;
		boolean isInside = false;
		Axis axis = facing.getAxis();
		
		Axis one = RotationUtils.getDifferentAxisFirst(axis);
    	Axis two = RotationUtils.getDifferentAxisSecond(axis);
    	
    	double minOne = CreativeAxisAlignedBB.getMin(other, one);
    	double minTwo = CreativeAxisAlignedBB.getMin(other, two);
    	double maxOne = CreativeAxisAlignedBB.getMax(other, one);
    	double maxTwo = CreativeAxisAlignedBB.getMax(other, two);
		
		Matrix3d rotationX = origin.rotationX();
		Matrix3d rotationY = origin.rotationY();
		Matrix3d rotationZ = origin.rotationZ();
		Vector3d rotationCenter = origin.axis();
		Vector3d translation = origin.translation();
		
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			vec.sub(rotationCenter);
			rotationX.transform(vec);
			rotationY.transform(vec);
			rotationZ.transform(vec);
			vec.add(rotationCenter);
			
			vec.add(translation);
			
			double vectorValue = RotationUtils.get(axis, vec);
			if(selected == null || (positive ? vectorValue > value : vectorValue < value))
			{
				selected = BoxCorner.values()[i];
				value = vectorValue;
			}
		}
		
		return new Vector3d[] {corners[selected.ordinal()], corners[selected.neighborOne.ordinal()], corners[selected.neighborTwo.ordinal()], corners[selected.neighborThree.ordinal()]};
	}
	
	public static enum BoxCorner
	{
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
		
		private void initCorner()
		{
			neighborOne = getCorner(x.getOpposite(), y, z);
			neighborTwo = getCorner(x, y.getOpposite(), z);
			neighborThree = getCorner(x, y, z.getOpposite());
		}
		
		public Vector3d getVector(AxisAlignedBB box)
		{
			return new Vector3d(CreativeAxisAlignedBB.getCornerX(box, this), CreativeAxisAlignedBB.getCornerY(box, this), CreativeAxisAlignedBB.getCornerZ(box, this));
		}
		
		public boolean isFacingPositive(Axis axis)
		{
			return getFacing(axis).getAxisDirection() == AxisDirection.POSITIVE;
		}
		
		public EnumFacing getFacing(Axis axis)
		{
			switch(axis)
			{
			case X:
				return x;
			case Y:
				return y;
			case Z:
				return z;
			}
			return null;
		}
		
		public BoxCorner flip(Axis axis)
		{
			switch(axis)
			{
			case X:
				return getCorner(x.getOpposite(), y, z);
			case Y:
				return getCorner(x, y.getOpposite(), z);
			case Z:
				return getCorner(x, y, z.getOpposite());
			}
			return null;
		}
		
		public BoxCorner rotate(Rotation rotation)
		{
			int normalX = x.getAxisDirection().getOffset();
			int normalY = y.getAxisDirection().getOffset();
			int normalZ = z.getAxisDirection().getOffset();
			return getCorner(EnumFacing.getFacingFromAxis(rotation.getMatrix().getX(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.X),
					EnumFacing.getFacingFromAxis(rotation.getMatrix().getY(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.Y),
					EnumFacing.getFacingFromAxis(rotation.getMatrix().getZ(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.Z));
		}
		
		public static BoxCorner getCornerUnsorted(EnumFacing facing)
		{
			switch(facing.getAxis())
			{
			case X:
				return getCorner(facing, EnumFacing.UP, EnumFacing.SOUTH);
			case Y:
				return getCorner(EnumFacing.EAST, facing, EnumFacing.SOUTH);
			case Z:
				return getCorner(EnumFacing.EAST, EnumFacing.UP, facing);
			}
			return null;
		}
		
		public static BoxCorner getCornerUnsorted(EnumFacing facing, EnumFacing facing2, EnumFacing facing3)
		{
			return getCorner(facing.getAxis() != Axis.X ? facing2.getAxis() != Axis.X ? facing3 : facing2 : facing,
					facing.getAxis() != Axis.Y ? facing2.getAxis() != Axis.Y ? facing3 : facing2 : facing, 
					facing.getAxis() != Axis.Z ? facing2.getAxis() != Axis.Z ? facing3 : facing2 : facing);
		}
		
		public static BoxCorner getCorner(EnumFacing x, EnumFacing y, EnumFacing z)
		{
			for (BoxCorner corner : BoxCorner.values()) {
				if(corner.x == x && corner.y == y && corner.z == z)
					return corner;
			}
			return null;
		}
		
		private static void initCorners()
		{
			for (BoxCorner corner : BoxCorner.values()) {
				corner.initCorner();
			}
		}
		
	}
	
	static
	{
		BoxCorner.initCorners();
	}
}
