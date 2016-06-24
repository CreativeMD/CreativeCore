package com.creativemd.creativecore.common.utils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class RotationUtils {
	
	public static enum Axis {
		
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
	}
	
	public static Vec3d applyVectorRotation(Vec3d vector, EnumFacing EnumFacing)
	{
		return applyVectorRotation(vector, Rotation.getRotationByFacing(EnumFacing));
	}
	
	public static Vec3d applyVectorRotation(Vec3d vector, Rotation EnumFacing)
	{
		double tempX = vector.xCoord;
		double tempY = vector.yCoord;
		double tempZ = vector.zCoord;
		
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
		double minX = cube.minX;
		double minY = cube.minY;
		double minZ = cube.minZ;
		double maxX = cube.maxX;
		double maxY = cube.maxY;
		double maxZ = cube.maxZ;
		if(center != null)
		{
			minX -= center.xCoord;
			minY -= center.yCoord;
			minZ -= center.zCoord;
			maxX -= center.xCoord;
			maxY -= center.yCoord;
			maxZ -= center.zCoord;
		}
		Vec3d min = applyVectorRotation(new Vec3d(minX, minY, minZ), EnumFacing);
		Vec3d max = applyVectorRotation(new Vec3d(maxX, maxY, maxZ), EnumFacing);
		
		if(center != null)
		{
			min = min.addVector(center.xCoord, center.yCoord, center.zCoord);
			max = max.addVector(center.xCoord, center.yCoord, center.zCoord);
		}
		
		if(min.xCoord < max.xCoord)
		{
			cube.minX = min.xCoord;
			cube.maxX = max.xCoord;
		}
		else
		{
			cube.minX = max.xCoord;
			cube.maxX = min.xCoord;
		}
		if(min.yCoord < max.yCoord)
		{
			cube.minY = min.yCoord;
			cube.maxY = max.yCoord;
		}
		else
		{
			cube.minY = max.yCoord;
			cube.maxY = min.yCoord;
		}
		if(min.zCoord < max.zCoord)
		{
			cube.minZ = min.zCoord;
			cube.maxZ = max.zCoord;
		}
		else
		{
			cube.minZ = max.zCoord;
			cube.maxZ = min.zCoord;
		}
	}
	
	public static EnumFacing getEnumFacingFromVec(Vec3d vec)
	{
		if(vec.xCoord == 1 && vec.yCoord == 0 && vec.zCoord == 0)
			return EnumFacing.EAST;
		if(vec.xCoord == -1 && vec.yCoord == 0 && vec.zCoord == 0)
			return EnumFacing.WEST;
		if(vec.xCoord == 0 && vec.yCoord == 1 && vec.zCoord == 0)
			return EnumFacing.UP;
		if(vec.xCoord == 0 && vec.yCoord == -1 && vec.zCoord == 0)
			return EnumFacing.DOWN;
		if(vec.xCoord == 0 && vec.yCoord == 0 && vec.zCoord == 1)
			return EnumFacing.SOUTH;
		if(vec.xCoord == 0 && vec.yCoord == 0 && vec.zCoord == -1)
			return EnumFacing.NORTH;
		return null;
	}

	public static EnumFacing rotateEnumFacing(EnumFacing EnumFacing, EnumFacing EnumFacing2) {
		Vec3d vec = new Vec3d(EnumFacing.getDirectionVec());
		return getEnumFacingFromVec(applyVectorRotation(vec, EnumFacing2));
	}
	
}
