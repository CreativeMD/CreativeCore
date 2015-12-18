package com.creativemd.creativecore.common.utils;


import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class RotationUtils {
	
	public static enum Axis {
		
		Xaxis, Yaxis, Zaxis;
		
		public boolean isAxis(ForgeDirection direction)
		{
			switch (this) {
			case Xaxis:
				return direction == ForgeDirection.EAST || direction == ForgeDirection.WEST;
			case Yaxis:
				return direction == ForgeDirection.UP || direction == ForgeDirection.DOWN;
			case Zaxis:
				return direction == ForgeDirection.SOUTH || direction == ForgeDirection.NORTH;
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
		
		public ForgeDirection getDirection()
		{
			switch (this) {
			case Xaxis:
				return ForgeDirection.EAST;
			case Yaxis:
				return ForgeDirection.UP;
			case Zaxis:
				return ForgeDirection.SOUTH;
			default:
				return ForgeDirection.UNKNOWN;
			}
		}
		
		public static Axis getAxis(ForgeDirection direction)
		{
			if(direction == ForgeDirection.EAST || direction == ForgeDirection.WEST)
				return Axis.Xaxis;
			if(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN)
				return Axis.Yaxis;
			if(direction == ForgeDirection.SOUTH || direction == ForgeDirection.NORTH)
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
	
	public static boolean isNegative(ForgeDirection direction)
	{
		return direction == ForgeDirection.WEST || direction == ForgeDirection.DOWN || direction == ForgeDirection.NORTH;
	}
	
	public static void applyDirection(ForgeDirection direction, ChunkCoordinates coord)
	{
		switch(direction)
		{
		case DOWN:
			coord.posY--;
			break;
		case EAST:
			coord.posX++;
			break;
		case NORTH:
			coord.posZ--;
			break;
		case SOUTH:
			coord.posZ++;
			break;
		case UP:
			coord.posY++;
			break;
		case WEST:
			coord.posX--;
			break;
		default:
			break;
		}
	}
	
	public static int getIndex(ForgeDirection direction)
	{
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			if(ForgeDirection.VALID_DIRECTIONS[i] == direction)
				return i;
		}
        return -1;
	}
	
	public static Vec3 applyVectorRotation(Vec3 vector, ForgeDirection direction)
	{
		return applyVectorRotation(vector, Rotation.getRotationByDirection(direction));
	}
	
	public static Vec3 applyVectorRotation(Vec3 vector, Rotation direction)
	{
		double tempX = vector.xCoord;
		double tempY = vector.yCoord;
		double tempZ = vector.zCoord;
		
		switch(direction)
		{
		case UP:
			vector.xCoord = -tempY;
			vector.yCoord = tempX;
			break;
		case DOWN:
			vector.xCoord = tempY;
			vector.yCoord = -tempX;
			break;
		case UPX:
			vector.zCoord = -tempY;
			vector.yCoord = tempZ;
			break;
		case DOWNX:
			vector.zCoord = tempY;
			vector.yCoord = -tempZ;
			break;
		case SOUTH:
			vector.xCoord = -tempZ;
			vector.zCoord = tempX;
			break;
		case NORTH:
			vector.xCoord = tempZ;
			vector.zCoord = -tempX;
			break;
		case WEST:
			vector.xCoord = -tempX;
			vector.zCoord = -tempZ;
			break;
		default:
			break;
		}
		return vector;
	}
	
	public static void applyCubeRotation(CubeObject cube, ForgeDirection direction)
	{
		applyCubeRotation(cube, Rotation.getRotationByDirection(direction), Vec3.createVectorHelper(0.5, 0.5, 0.5));
	}
	
	public static void applyCubeRotation(CubeObject cube, Rotation direction, Vec3 center)
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
		Vec3 min = applyVectorRotation(Vec3.createVectorHelper(minX, minY, minZ), direction);
		Vec3 max = applyVectorRotation(Vec3.createVectorHelper(maxX, maxY, maxZ), direction);
		
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
	
	public static ForgeDirection getDirectionFromVec(Vec3 vec)
	{
		if(vec.xCoord == 1 && vec.yCoord == 0 && vec.zCoord == 0)
			return ForgeDirection.EAST;
		if(vec.xCoord == -1 && vec.yCoord == 0 && vec.zCoord == 0)
			return ForgeDirection.WEST;
		if(vec.xCoord == 0 && vec.yCoord == 1 && vec.zCoord == 0)
			return ForgeDirection.UP;
		if(vec.xCoord == 0 && vec.yCoord == -1 && vec.zCoord == 0)
			return ForgeDirection.DOWN;
		if(vec.xCoord == 0 && vec.yCoord == 0 && vec.zCoord == 1)
			return ForgeDirection.SOUTH;
		if(vec.xCoord == 0 && vec.yCoord == 0 && vec.zCoord == -1)
			return ForgeDirection.NORTH;
		return ForgeDirection.UNKNOWN;
	}

	public static ForgeDirection rotateForgeDirection(ForgeDirection direction, ForgeDirection direction2) {
		Vec3 vec = Vec3.createVectorHelper(direction.offsetX, direction.offsetY, direction.offsetZ);
		return getDirectionFromVec(applyVectorRotation(vec, direction2));
	}
	
}
