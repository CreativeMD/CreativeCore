package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
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
	
	public void add(Vec3d vec)
	{
		this.minX += vec.x;
		this.minY += vec.y;
		this.minZ += vec.z;
		this.maxX += vec.x;
		this.maxY += vec.y;
		this.maxZ += vec.z;
	}
	
	public void sub(Vec3d vec)
	{
		this.minX -= vec.x;
		this.minY -= vec.y;
		this.minZ -= vec.z;
		this.maxX -= vec.x;
		this.maxY -= vec.y;
		this.maxZ -= vec.z;
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
				size = new Vec3d(Math.max(size.x, newSize.x), Math.max(size.y, newSize.y), Math.max(size.z, newSize.z));
			}
		}
		return size;
	}
	
	public void rotate(EnumFacing facing, Vector3f center)
	{
		Matrix3f matrix = new Matrix3f();
		if(facing.getAxis() == Axis.X)
			facing = facing.getOpposite();
		matrix.rotY((float) Math.toRadians(facing.getHorizontalAngle()));
		rotate(matrix, center);
	}
	
	public void rotate(Matrix3f matrix, Vector3f center)
	{
		Vector3f low = new Vector3f(minX, minY, minZ);
		Vector3f high = new Vector3f(maxX, maxY, maxZ);
		
		low.sub(center);
		high.sub(center);
		
		matrix.transform(low);
		matrix.transform(high);
		
		low.add(center);
		high.add(center);
		
		set(low.x, low.y, low.z, high.x, high.y, high.z);
	}
	
	public void set(float x, float y, float z, float x2, float y2, float z2)
	{
		this.minX = Math.min(x, x2);
		this.minY = Math.min(y, y2);
		this.minZ = Math.min(z, z2);
		this.maxX = Math.max(x, x2);
		this.maxY = Math.max(y, y2);
		this.maxZ = Math.max(z, z2);
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

	public CubeObject flip(EnumFacing facing) {
		CubeObject cube = new CubeObject(this);
		switch(facing)
		{
		case EAST:
		case WEST:
			cube.maxX = 1-this.minX;
			cube.minX = 1-this.maxX;
			break;
		case UP:
		case DOWN:
			cube.maxY = 1-this.minY;
			cube.minY = 1-this.maxY;
			break;
		case SOUTH:
		case NORTH:
			cube.maxZ = 1-this.minZ;
			cube.minZ = 1-this.maxZ;
			break;
		}
		return cube;
	}
}
