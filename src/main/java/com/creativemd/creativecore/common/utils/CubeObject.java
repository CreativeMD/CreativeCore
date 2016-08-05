package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class CubeObject {
	
	public float minX;
	public float minY;
	public float minZ;
	public float maxX;
	public float maxY;
	public float maxZ;
	
	public CubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
	{
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public CubeObject(AxisAlignedBB box)
	{
		this((float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ);
	}
	
	public CubeObject()
	{
		this(0, 0, 0, 1, 1, 1);
	}
	
	public CubeObject(CubeObject cube)
	{
		this(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ, cube);
	}
	
	public CubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, CubeObject cube)
	{
		this(minX, minY, minZ, maxX, maxY, maxZ);
		applyExtraCubeData(cube);
	}
	
	protected void applyExtraCubeData(CubeObject cube)
	{
		
	}
	
	/*public CubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean normalBlock)
	{
		this(minX, minY, minZ, maxX, maxY, maxZ);
		this.normalBlock = normalBlock;
	}*/
	
	public Vec3d getSize()
	{
		return new Vec3d(maxX-minX, maxY-minY, maxZ-minZ);
	}
	
	@Override
	public String toString()
    {
        return "cube[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
	
	public AxisAlignedBB getAxis()
	{
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public static Vec3d getSizeOfCubes(ArrayList<? extends CubeObject> cubes)
	{
		if(cubes.size() == 0)
			return new Vec3d(0, 0, 0);
		CubeObject cube = new CubeObject(cubes.get(0));
		for (int i = 1; i < cubes.size(); i++) {
			cube.minX = Math.min(cube.minX, cubes.get(i).minX);
			cube.minY = Math.min(cube.minY, cubes.get(i).minY);
			cube.minZ = Math.min(cube.minZ, cubes.get(i).minZ);
			cube.maxX = Math.max(cube.maxX, cubes.get(i).maxX);
			cube.maxY = Math.max(cube.maxY, cubes.get(i).maxY);
			cube.maxZ = Math.max(cube.maxZ, cubes.get(i).maxZ);
		}
		return cube.getSize();
	}
	
	public static Vec3d getBigestCubeSize(ArrayList<CubeObject> cubes)
	{
		Vec3d size = null;
		for (int i = 0; i < cubes.size(); i++) {
			Vec3d newSize = cubes.get(i).getSize();
			if(size == null)
				size = newSize;
			else
			{
				size = new Vec3d(Math.max(size.xCoord, newSize.xCoord), Math.max(size.yCoord, newSize.yCoord), Math.max(size.zCoord, newSize.zCoord));
			}
		}
		return size;
	}
	
	public BlockPos getOffset()
	{
		return new BlockPos(minX, minY, minZ);
	}
	
	public CubeObject offset(BlockPos pos)
	{
		return new CubeObject(minX-pos.getX(), minY-pos.getY(), minZ-pos.getZ(), maxX-pos.getX(), maxY-pos.getY(), maxZ-pos.getZ(), this);
	}
	
	public static CubeObject rotateCube(CubeObject cube, EnumFacing direction)
	{
		return rotateCube(cube, direction, new Vec3d(0.5, 0.5, 0.5));
	}
	
	public static CubeObject rotateCube(CubeObject cube, EnumFacing direction, Vec3d center)
	{
		return rotateCube(cube, Rotation.getRotationByFacing(direction), center);
	}
	
	public static CubeObject rotateCube(CubeObject cube, Rotation direction, Vec3d center)
	{
		CubeObject rotateCube = new CubeObject(cube);
		RotationUtils.applyCubeRotation(rotateCube, direction, center);
		return rotateCube;
	}
	
	public float getVertexInformationPositionOposite(int index) {
		switch(EnumFacing.getFront(index).getOpposite())
		{
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
	
	public float getVertexInformationPosition(int index) {
		switch(EnumFacing.getFront(index))
		{
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
}
