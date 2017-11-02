package com.creativemd.creativecore.common.utils;

import javax.vecmath.Matrix3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

import net.minecraft.util.math.Vec3i;

public class RotationMatrix {
	
	public int m00;
	public int m01;
	public int m02;
	public int m10;
	public int m11;
	public int m12;
	public int m20;
	public int m21;
	public int m22;


	public RotationMatrix(int m00, int m01, int m02,
			int m10, int m11, int m12,
			int m20, int m21, int m22)
    {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
	
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
	
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
    }
	
	public int getX(int[] vec)
	{
		return getX(vec[0], vec[1], vec[2]);
	}
	
	public int getX(Vec3i vec)
	{
		return getX(vec.getX(), vec.getY(), vec.getZ());
	}
	
	public int getX(int x, int y, int z)
	{
		return x * m00 + y * m01 + z * m02;
	}
	
	
	public long getX(long x, long y, long z)
	{
		return x * m00 + y * m01 + z * m02;
	}
	
	public int getY(int[] vec)
	{
		return getY(vec[0], vec[1], vec[2]);
	}
	
	public int getY(Vec3i vec)
	{
		return getY(vec.getX(), vec.getY(), vec.getZ());
	}
	
	public int getY(int x, int y, int z)
	{
		return x * m10 + y * m11 + z * m12;
	}
	
	public long getY(long x, long y, long z)
	{
		return x * m10 + y * m11 + z * m12;
	}
	
	public int getZ(int[] vec)
	{
		return getZ(vec[0], vec[1], vec[2]);
	}
	
	public int getZ(Vec3i vec)
	{
		return getZ(vec.getX(), vec.getY(), vec.getZ());
	}
	
	public int getZ(int x, int y, int z)
	{
		return x * m20 + y * m21 + z * m22;
	}
	
	public long getZ(long x, long y, long z)
	{
		return x * m20 + y * m21 + z * m22;
	}
	
	public Vec3i transform(Vec3i vec)
	{
		int x = vec.getX() * m00 + vec.getY() * m01 + vec.getZ() * m02;
		int y = vec.getX() * m10 + vec.getY() * m11 + vec.getZ() * m12;
		int z = vec.getX() * m20 + vec.getY() * m21 + vec.getZ() * m22;
		return new Vec3i(x, y, z);
	}
	
	public void transform(Tuple3f triple)
	{
		float x = triple.x * m00 + triple.y * m01 + triple.z * m02;
		float y = triple.x * m10 + triple.y * m11 + triple.z * m12;
		float z = triple.x * m20 + triple.y * m21 + triple.z * m22;
		triple.set(x, y, z);
	}
	
	public void transform(Tuple3d triple)
	{
		double x = triple.x * m00 + triple.y * m01 + triple.z * m02;
		double y = triple.x * m10 + triple.y * m11 + triple.z * m12;
		double z = triple.x * m20 + triple.y * m21 + triple.z * m22;
		triple.set(x, y, z);
	}
	
}
