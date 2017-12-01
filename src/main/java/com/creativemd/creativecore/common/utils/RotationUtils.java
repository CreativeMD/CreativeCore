package com.creativemd.creativecore.common.utils;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class RotationUtils {
	
	private static String[] facingNames;
	private static String[] horizontalFacingNames;
	
	public static String[] getHorizontalFacingNames()
	{
		if(horizontalFacingNames == null)
		{
			horizontalFacingNames = new String[4];
			for (int i = 0; i < horizontalFacingNames.length; i++) {
				horizontalFacingNames[i] = EnumFacing.getHorizontal(i).getName();
			}
		}
		return horizontalFacingNames;
	}
	
	public static String[] getFacingNames()
	{
		if(facingNames == null)
		{
			facingNames = new String[6];
			for (int i = 0; i < facingNames.length; i++) {
				facingNames[i] = EnumFacing.getFront(i).getName();
			}
		}
		return facingNames;
	}
	
	public static EnumFacing getFacing(Axis axis)
	{
		switch(axis)
		{
		case X:
			return EnumFacing.EAST;
		case Y:
			return EnumFacing.UP;
		case Z:
			return EnumFacing.NORTH;
		}
		return null;
	}
	
	public static void setValue(Tuple3d vec, double value, Axis axis)
	{
		switch (axis)
		{
		case X:
			vec.x = value;
			break;
		case Y:
			vec.y = value;
			break;
		case Z:
			vec.z = value;
			break;
		}
	}
	
	public static void setValue(Tuple3f vec, float value, Axis axis)
	{
		switch (axis)
		{
		case X:
			vec.x = value;
			break;
		case Y:
			vec.y = value;
			break;
		case Z:
			vec.z = value;
			break;
		}
	}
	
	public static Vec3d setValue(Vec3d vec, double value, Axis axis)
	{
		switch (axis)
		{
		case X:
			return new Vec3d(value, vec.yCoord, vec.zCoord);
		case Y:
			return new Vec3d(vec.xCoord, value, vec.zCoord);
		case Z:
			return new Vec3d(vec.xCoord, vec.yCoord, value);
		}
		return null;
	}
	
	public static double get(Axis axis, Tuple3d vec)
	{
		return get(axis, vec.x, vec.y, vec.z);
	}
	
	public static float get(Axis axis, Tuple3f vec)
	{
		return get(axis, vec.x, vec.y, vec.z);
	}
	
	public static double get(Axis axis, Vec3d vec)
	{
		return get(axis, vec.xCoord, vec.yCoord, vec.zCoord);
	}
	
	public static int get(Axis axis, Vec3i vec)
	{
		return get(axis, vec.getX(), vec.getY(), vec.getZ());
	}
	
	public static float get(Axis axis, float x, float y, float z)
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
		return 0;
	}
	
	public static double get(Axis axis, double x, double y, double z)
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
		return 0;
	}
	
	public static int get(Axis axis, int x, int y, int z)
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
		return 0;
	}
	
	public static Axis getDifferentAxis(Axis one, Axis two)
	{
		switch(one)
		{
		case X:
			if(two == Axis.Y)
				return Axis.Z;
			return Axis.Y;
		case Y:
			if(two == Axis.X)
				return Axis.Z;
			return Axis.X;
		case Z:
			if(two == Axis.Y)
				return Axis.X;
			return Axis.Y;
		}
		return null;
	}
	
	public static Axis getDifferentAxisFirst(Axis axis)
	{
		switch(axis)
		{
		case X:
			return Axis.Y;
		case Y:
			return Axis.Z;
		case Z:
			return Axis.X;
		}
		return axis;
	}
	
	public static Axis getDifferentAxisSecond(Axis axis)
	{
		switch(axis)
		{
		case X:
			return Axis.Z;
		case Y:
			return Axis.X;
		case Z:
			return Axis.Y;
		}
		return axis;
	}
	
	public static Axis rotateAxis(Axis axis, Rotation rotation)
	{
		if(axis == rotation.axis)
			return axis;
		
		switch(axis)
		{
		case X:
			if(rotation.axis == Axis.Y)
				return Axis.Z;
			return Axis.Y;
		case Y:
			if(rotation.axis == Axis.Z)
				return Axis.X;
			return Axis.Y;
		case Z:
			if(rotation.axis == Axis.X)
				return Axis.Y;
			return Axis.X;
		}
		return axis;
	}
	
	public static EnumFacing rotateFacing(EnumFacing facing, Rotation rotation)
	{
		Vec3i rotatedNormal = new Vec3i(rotation.getMatrix().getX(facing.getDirectionVec()), rotation.getMatrix().getY(facing.getDirectionVec()), rotation.getMatrix().getZ(facing.getDirectionVec()));
		for (EnumFacing rotated : EnumFacing.VALUES) {
			if(rotated.getDirectionVec().equals(rotatedNormal))
				return rotated;
		}
		return facing;
	}
	
	public static Vec3i rotateVec(Vec3i vec, Rotation rotation)
	{
		return rotation.getMatrix().transform(vec);
	}

	public static void rotateVec(Vector3f vector, Rotation rotation)
	{
		rotation.getMatrix().transform(vector);
	}
	
	public static void rotateVec(Vector3d vector, Rotation rotation)
	{
		rotation.getMatrix().transform(vector);
	}

	public static boolean isFacingPositive(int index)
	{
		return index == 1 || index == 3 || index == 5;
	}
	
	public static float getUFromFacing(EnumFacing facing, float x, float y, float z)
	{
		switch(facing.getAxis())
		{
		case X:
			return z;
		case Y:
			return x;
		case Z:
			return x;
		}
		return 0;
	}
	
	public static float getVFromFacing(EnumFacing facing, float x, float y, float z)
	{
		switch(facing.getAxis())
		{
		case X:
			return y;
		case Y:
			return z;
		case Z:
			return y;
		}
		return 0;
	}
	
}
