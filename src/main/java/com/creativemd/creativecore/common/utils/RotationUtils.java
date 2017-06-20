package com.creativemd.creativecore.common.utils;

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
	
	public static int getAxisIndex(Axis axis)
	{
		switch (axis) {
		case X:
			return 0;
		case Y:
			return 1;
		case Z:
			return 2;
		default:
			return 0;
		}
	}
	
	public static Axis getAxisFromIndex(int index)
	{
		if(index == 0)
			return Axis.X;
		if(index == 1)
			return Axis.Y;
		if(index == 2)
			return Axis.Z;
		return null;
	}
	
	public static EnumFacing getFacingFromAxis(Axis axis)
	{
		switch (axis) {
		case X:
			return EnumFacing.EAST;
		case Y:
			return EnumFacing.UP;
		case Z:
			return EnumFacing.SOUTH;
		default:
			return null;
		}
	}
	
	/*public static enum Axis {
		
		Xaxis, Yaxis, Zaxis;
		
		public boolean isAxis(EnumFacing EnumFacing)
		{
			switch (this) {
			case Xaxis:
				return EnumFacing == EnumFacing.EAST || EnumFacing == EnumFacing.WEST;
			case Yaxis:
				return EnumFacing == EnumFacing.UP || EnumFacing == EnumFacing.DOWN;
			case Zaxis:
				return EnumFacing == EnumFacing.SOUTH || EnumFacing == EnumFacing.NORTH;
			default:
				return false;
			}
		}
		
		public int toInt()
		{
			switch (this) {
			case Xaxis:
				return 0;
			case Yaxis:
				return 1;
			case Zaxis:
				return 2;
			default:
				return 0;
			}
		}
		
		public EnumFacing getEnumFacing()
		{
			switch (this) {
			case Xaxis:
				return EnumFacing.EAST;
			case Yaxis:
				return EnumFacing.UP;
			case Zaxis:
				return EnumFacing.SOUTH;
			default:
				return null;
			}
		}
		
		public static Axis getAxis(EnumFacing EnumFacing)
		{
			if(EnumFacing == EnumFacing.EAST || EnumFacing == EnumFacing.WEST)
				return Axis.Xaxis;
			if(EnumFacing == EnumFacing.UP || EnumFacing == EnumFacing.DOWN)
				return Axis.Yaxis;
			if(EnumFacing == EnumFacing.SOUTH || EnumFacing == EnumFacing.NORTH)
				return Axis.Zaxis;
			return null;
		}
		
		public static Axis getAxis(int id)
		{
			if(id == 0)
				return Xaxis;
			if(id == 1)
				return Yaxis;
			if(id == 2)
				return Zaxis;
			return null;
		}
	}*/
	
	public static Vec3d applyVectorRotation(Vec3d vector, EnumFacing EnumFacing)
	{
		return applyVectorRotation(vector, Rotation.getRotationByFacing(EnumFacing));
	}
	
	public static Vec3d applyVectorRotation(Vec3d vector, Rotation EnumFacing)
	{
		double tempX = vector.x;
		double tempY = vector.y;
		double tempZ = vector.z;
		
		double posX = tempX;
		double posY = tempY;
		double posZ = tempZ;
		
		switch(EnumFacing)
		{
		case UP:
			posX = -tempY;
			posY = tempX;
			break;
		case DOWN:
			posX = tempY;
			posY = -tempX;
			break;
		case UPX:
			posZ = -tempY;
			posY = tempZ;
			break;
		case DOWNX:
			posZ = tempY;
			posY = -tempZ;
			break;
		case SOUTH:
			posX = -tempZ;
			posZ = tempX;
			break;
		case NORTH:
			posX = tempZ;
			posZ = -tempX;
			break;
		case WEST:
			posX = -tempX;
			posZ = -tempZ;
			break;
		default:
			break;
		}
		return new Vec3d(posX, posY, posZ);
	}
	
	public static void applyCubeRotation(CubeObject cube, EnumFacing EnumFacing)
	{
		applyCubeRotation(cube, Rotation.getRotationByFacing(EnumFacing), new Vec3d(0.5, 0.5, 0.5));
	}
	
	public static void applyCubeRotation(CubeObject cube, Rotation EnumFacing, Vec3d center)
	{
		float minX = cube.minX;
		float minY = cube.minY;
		float minZ = cube.minZ;
		float maxX = cube.maxX;
		float maxY = cube.maxY;
		float maxZ = cube.maxZ;
		if(center != null)
		{
			minX -= center.x;
			minY -= center.y;
			minZ -= center.z;
			maxX -= center.x;
			maxY -= center.y;
			maxZ -= center.z;
		}
		Vec3d min = applyVectorRotation(new Vec3d(minX, minY, minZ), EnumFacing);
		Vec3d max = applyVectorRotation(new Vec3d(maxX, maxY, maxZ), EnumFacing);
		
		if(center != null)
		{
			min = min.addVector(center.x, center.y, center.z);
			max = max.addVector(center.x, center.y, center.z);
		}
		
		if(min.x < max.x)
		{
			cube.minX = (float)min.x;
			cube.maxX = (float)max.x;
		}
		else
		{
			cube.minX = (float)max.x;
			cube.maxX = (float)min.x;
		}
		if(min.y < max.y)
		{
			cube.minY = (float)min.y;
			cube.maxY = (float)max.y;
		}
		else
		{
			cube.minY = (float)max.y;
			cube.maxY = (float)min.y;
		}
		if(min.z < max.z)
		{
			cube.minZ = (float)min.z;
			cube.maxZ = (float)max.z;
		}
		else
		{
			cube.minZ = (float)max.z;
			cube.maxZ = (float)min.z;
		}
	}
	
	public static EnumFacing getFacingFromVec(Vec3d vec)
	{
		if(vec.x == 1 && vec.y == 0 && vec.z == 0)
			return EnumFacing.EAST;
		if(vec.x == -1 && vec.y == 0 && vec.z == 0)
			return EnumFacing.WEST;
		if(vec.x == 0 && vec.y == 1 && vec.z == 0)
			return EnumFacing.UP;
		if(vec.x == 0 && vec.y == -1 && vec.z == 0)
			return EnumFacing.DOWN;
		if(vec.x == 0 && vec.y == 0 && vec.z == 1)
			return EnumFacing.SOUTH;
		if(vec.x == 0 && vec.y == 0 && vec.z == -1)
			return EnumFacing.NORTH;
		return null;
	}

	public static EnumFacing rotateFacing(EnumFacing facing, EnumFacing facing2) {
		Vec3d vec = new Vec3d(facing.getDirectionVec());
		vec = applyVectorRotation(vec, facing2);
		return EnumFacing.getFacingFromVector((float) vec.x, (float) vec.y, (float) vec.z);
		//return getFacingFromVec(vec);
	}
	
}
