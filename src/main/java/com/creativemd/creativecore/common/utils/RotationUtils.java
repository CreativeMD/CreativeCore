package com.creativemd.creativecore.common.utils;


import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class RotationUtils {
	
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
		double minX = cube.minX-0.5D;
		double minY = cube.minY-0.5D;
		double minZ = cube.minZ-0.5D;
		double maxX = cube.maxX-0.5D;
		double maxY = cube.maxY-0.5D;
		double maxZ = cube.maxZ-0.5D;
		Vec3 min = applyVectorRotation(Vec3.createVectorHelper(minX, minY, minZ), direction);
		Vec3 max = applyVectorRotation(Vec3.createVectorHelper(maxX, maxY, maxZ), direction);
		
		min = min.addVector(0.5, 0.5, 0.5);
		max = max.addVector(0.5, 0.5, 0.5);
		
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
	
}
