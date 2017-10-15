package com.creativemd.creativecore.common.utils;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import net.minecraft.client.renderer.EnumFaceDirection.VertexInformation;
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
	
	public void rotate(Rotation rotation, Vector3f center)
	{
		Vector3f low = new Vector3f(minX, minY, minZ);
		Vector3f high = new Vector3f(maxX, maxY, maxZ);
		
		low.sub(center);
		high.sub(center);
		
		rotation.getMatrix().transform(low);
		rotation.getMatrix().transform(high);
		
		low.add(center);
		high.add(center);
		
		set(low.x, low.y, low.z, high.x, high.y, high.z);
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
	
	/*public float getVertexInformationPositionX(VertexInformation info)
	{
		return getVertexInformationPosition(info.xIndex);
	}
	
	public float getVertexInformationPositionY(VertexInformation info)
	{
		return getVertexInformationPosition(info.yIndex);
	}
	
	public float getVertexInformationPositionZ(VertexInformation info)
	{
		return getVertexInformationPosition(info.zIndex);
	}*/
	
	public Vector3f get(VertexInformation info, Vector3f output)
	{
		output.set(getVertexInformationPosition(info.xIndex), getVertexInformationPosition(info.yIndex), getVertexInformationPosition(info.zIndex));
		return output;
	}
	
	public float getVertexInformationPositionOffset(int index, Vec3i pos)
	{
		return getVertexInformationPosition(index) - RotationUtils.get(EnumFacing.getFront(index).getAxis(), pos);
	}
	
	public float getVertexInformationPosition(int index)
	{
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
	
	public float getValueOfFacing(EnumFacing facing)
	{
		switch(facing)
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
	
	public float getSize(Axis axis)
	{
		switch (axis)
		{
		case X:
			return maxX - minX;
		case Y:
			return maxY - minY;
		case Z:
			return maxZ - minZ;
		}
		return 0;
	}
	
	public void setMin(Axis axis, float value)
	{
		switch (axis)
		{
		case X:
			minX = value;
			break;
		case Y:
			minY = value;
			break;
		case Z:
			minZ = value;
			break;
		}
	}
	
	public float getMin(Axis axis)
	{
		switch (axis)
		{
		case X:
			return minX;
		case Y:
			return minY;
		case Z:
			return minZ;
		}
		return 0;
	}
	
	public void setMax(Axis axis, float value)
	{
		switch (axis)
		{
		case X:
			maxX = value;
			break;
		case Y:
			maxY = value;
			break;
		case Z:
			maxZ = value;
			break;
		}
	}
	
	public float getMax(Axis axis)
	{
		switch (axis)
		{
		case X:
			return maxX;
		case Y:
			return maxY;
		case Z:
			return maxZ;
		}
		return 0;
	}
	
	//Old
	public static CubeObject rotateCube(CubeObject cube, EnumFacing direction)
	{
		return rotateCube(cube, direction, new Vec3d(0.5, 0.5, 0.5));
	}
	
	public static CubeObject rotateCube(CubeObject cube, EnumFacing direction, Vec3d center)
	{
		CubeObject rotateCube = new CubeObject(cube);
		applyCubeRotation(rotateCube, direction, center);
		return rotateCube;
	}
	
	public static Vec3d applyVectorRotation(Vec3d vector, EnumFacing EnumFacing)
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
		applyCubeRotation(cube, EnumFacing, new Vec3d(0.5, 0.5, 0.5));
	}
	
	public static void applyCubeRotation(CubeObject cube, EnumFacing EnumFacing, Vec3d center)
	{
		float minX = cube.minX;
		float minY = cube.minY;
		float minZ = cube.minZ;
		float maxX = cube.maxX;
		float maxY = cube.maxY;
		float maxZ = cube.maxZ;
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
			cube.minX = (float)min.xCoord;
			cube.maxX = (float)max.xCoord;
		}
		else
		{
			cube.minX = (float)max.xCoord;
			cube.maxX = (float)min.xCoord;
		}
		if(min.yCoord < max.yCoord)
		{
			cube.minY = (float)min.yCoord;
			cube.maxY = (float)max.yCoord;
		}
		else
		{
			cube.minY = (float)max.yCoord;
			cube.maxY = (float)min.yCoord;
		}
		if(min.zCoord < max.zCoord)
		{
			cube.minZ = (float)min.zCoord;
			cube.maxZ = (float)max.zCoord;
		}
		else
		{
			cube.minZ = (float)max.zCoord;
			cube.maxZ = (float)min.zCoord;
		}
	}
}
