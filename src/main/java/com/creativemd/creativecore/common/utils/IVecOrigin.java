package com.creativemd.creativecore.common.utils;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import net.minecraft.util.math.AxisAlignedBB;

public interface IVecOrigin {
	
	public double offX();
	public double offY();
	public double offZ();
	
	public double rotX();
	public double rotY();
	public double rotZ();
	
	public boolean isRotated();
	
	public void offX(double value);
	public void offY(double value);
	public void offZ(double value);
	
	public void rotX(double value);
	public void rotY(double value);
	public void rotZ(double value);
	
	public Vector3d axis();
	public Matrix3d rotationX();
	public Matrix3d rotationY();
	public Matrix3d rotationZ();
	public Vector3d translation();

}
