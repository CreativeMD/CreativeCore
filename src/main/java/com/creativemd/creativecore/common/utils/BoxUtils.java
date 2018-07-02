package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.collision.CreativeAxisAlignedBB;
import com.creativemd.creativecore.common.utils.RotationUtils.BooleanRotation;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class BoxUtils {
	
	public static Matrix3d createIdentityMatrix()
	{
		Matrix3d matrix = new Matrix3d();
		matrix.setIdentity();
		return matrix;
	}
	
	public static Matrix3d createRotationMatrix(double rotX, double rotY, double rotZ)
	{
		Matrix3d matrix = createRotationMatrixZ(rotZ);
		matrix.mul(createRotationMatrixY(rotY));
		matrix.mul(createRotationMatrixX(rotX));
		return matrix;
	}
	
	public static Matrix4d createRotationMatrixAndTranslation(double x, double y, double z, double rotX, double rotY, double rotZ)
	{
		Matrix4d matrix = new Matrix4d();
		matrix.rotZ(Math.toRadians(rotZ));
		
		Matrix4d temp = new Matrix4d();
		temp.rotY(Math.toRadians(rotY));
		matrix.mul(temp);
		
		temp = new Matrix4d();
		temp.rotX(Math.toRadians(rotX));
		matrix.mul(temp);
		
		matrix.setTranslation(new Vector3d(x, y, z));
		
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
		int i = 0;
		while(i < boxes.size()){
			int j = 0;
			while(j < boxes.size()) {
				if(i != j)
				{
					AxisAlignedBB box = combineBoxes(boxes.get(i), boxes.get(j), deviation);
					if(box != null)
					{
						if(i > j)
						{
							boxes.remove(i);
							boxes.remove(j);
							i--;
						}else{
							boxes.remove(j);
							boxes.remove(i);
						}
						j = 0;
						
						boxes.add(box);
						continue;
					}
				}
				j++;
			}
			i++;
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
	
	private static double lengthIgnoreAxis(Vector3d vec, Axis axis)
	{
		switch(axis)
		{
		case X:
			return Math.sqrt(vec.y*vec.y + vec.z*vec.z);
		case Y:
			return Math.sqrt(vec.x*vec.x + vec.z*vec.z);
		case Z:
			return Math.sqrt(vec.x*vec.x + vec.y*vec.y);
		default:
			return 0;
		}
	}
	
	private static void includeMaxRotationInBox(Box box, Vector3d vec, Axis axis, Matrix3d matrix, double rotation, Vector3d additionalTranslation)
	{
		if(matrix == null)
			return ;
		
		Double length = null;
		
		BooleanRotation state = BooleanRotation.getRotationState(axis, vec);
		
		if(rotation > 0)
		{
			double skipRotation = 90;
			while(skipRotation < rotation && skipRotation < 360)
			{
				EnumFacing facing = state.clockwiseMaxFacing();
				
				if(length == null)
					length = lengthIgnoreAxis(vec, axis);
				
				box.include(facing, length);
				if(additionalTranslation != null)
					box.include(facing, length + RotationUtils.get(facing.getAxis(), additionalTranslation));
				
				state = state.clockwise();
				skipRotation += 90;
			}
			
			matrix.transform(vec);
			
			if(skipRotation < 360 && !state.is(vec))
			{
				EnumFacing facing = state.clockwiseMaxFacing();
				
				if(length == null)
					length = lengthIgnoreAxis(vec, axis);
				
				box.include(facing, length);
				if(additionalTranslation != null)
					box.include(facing, length + RotationUtils.get(facing.getAxis(), additionalTranslation));
			}
		}
		else
		{
			double skipRotation = -90;
			while(skipRotation > rotation && skipRotation > -360)
			{
				EnumFacing facing = state.counterMaxClockwiseFacing();
				
				if(length == null)
					length = lengthIgnoreAxis(vec, axis);
				
				box.include(facing, length);
				if(additionalTranslation != null)
					box.include(facing, length + RotationUtils.get(facing.getAxis(), additionalTranslation));
				
				state = state.counterClockwise();
				skipRotation -= 90;
			}
			
			matrix.transform(vec);
			
			if(skipRotation > -360 && !state.is(vec))
			{
				EnumFacing facing = state.counterMaxClockwiseFacing();
				
				if(length == null)
					length = lengthIgnoreAxis(vec, axis);
				
				box.include(facing, length);
				if(additionalTranslation != null)
					box.include(facing, length + RotationUtils.get(facing.getAxis(), additionalTranslation));
			}
		}
	}
	
	public static AxisAlignedBB getRotatedSurrounding(AxisAlignedBB box, Vector3d rotationCenter, Matrix3d initRotation, Vector3d initTranslation, Matrix3d addRotX, double rotX, Matrix3d addRotY, double rotY, Matrix3d addRotZ, double rotZ, Vector3d additionalTranslation)
	{
		Vector3d[] corners = getCorners(box);
		
		Box bb = new Box();
		
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			vec.sub(rotationCenter);
			
			// Apply initial rotation
			initRotation.transform(vec);
			bb.include(vec);
			
			//Additional rotation and translation			
			includeMaxRotationInBox(bb, vec, Axis.X, addRotX, rotX, additionalTranslation);
			includeMaxRotationInBox(bb, vec, Axis.Y, addRotY, rotY, additionalTranslation);
			includeMaxRotationInBox(bb, vec, Axis.Z, addRotZ, rotZ, additionalTranslation);
			
			if(additionalTranslation != null)
				vec.add(additionalTranslation);
			
			bb.include(vec);
		}
		
		bb.translate(rotationCenter);
		bb.translate(initTranslation);
		
		return bb.getAxisBB();
	}
	
	public static AxisAlignedBB getRotated(AxisAlignedBB box, Vector3d rotationCenter, Matrix3d rotation, Vector3d translation)
	{
		Vector3d[] corners = getCorners(box);
		
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			vec.sub(rotationCenter);
			rotation.transform(vec);
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
	
	public static Vector3d[] getOuterCorner(EnumFacing facing, IVecOrigin origin, AxisAlignedBB box)
	{
		Vector3d[] corners = getCorners(box);
		
		boolean positive = facing.getAxisDirection() == AxisDirection.POSITIVE;
		double value = 0;
		BoxCorner selected = null;
		Axis axis = facing.getAxis();
		
		Matrix3d rotation = origin.rotation();
		Vector3d rotationCenter = origin.axis();
		Vector3d translation = origin.translation();
		
		for (int i = 0; i < corners.length; i++) {
			Vector3d vec = corners[i];
			vec.sub(rotationCenter);
			rotation.transform(vec);
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
	
	private static class Box {
		
		public double minX;
		public double minY;
		public double minZ;
		public double maxX;
		public double maxY;
		public double maxZ;
		
		public Box()
		{
			minX = Double.MAX_VALUE;
			minY = Double.MAX_VALUE;
			minZ = Double.MAX_VALUE;
			maxX = -Double.MAX_VALUE;
			maxY = -Double.MAX_VALUE;
			maxZ = -Double.MAX_VALUE;
		}
		
		public void include(Vector3d vec)
		{
			minX = Math.min(minX, vec.x);
			minY = Math.min(minY, vec.y);
			minZ = Math.min(minZ, vec.z);
			maxX = Math.max(maxX, vec.x);
			maxY = Math.max(maxY, vec.y);
			maxZ = Math.max(maxZ, vec.z);
		}
		
		public void include(EnumFacing facing, double value)
		{
			switch(facing)
			{
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
		
		public void translate(Vector3d translation)
		{
			minX += translation.x;
			minY += translation.y;
			minZ += translation.z;
			maxX += translation.x;
			maxY += translation.y;
			maxZ += translation.z;
		}
		
		public AxisAlignedBB getAxisBB()
		{
			return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		}
		
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
