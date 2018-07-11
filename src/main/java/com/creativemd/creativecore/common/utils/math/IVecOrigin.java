package com.creativemd.creativecore.common.utils.math;

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
	
	public void off(double x, double y, double z);
	
	public void rotX(double value);
	public void rotY(double value);
	public void rotZ(double value);
	
	public void rot(double x, double y, double z);
	
	public Vector3d axis();
	public Matrix3d rotation();
	public Matrix3d rotationInv();
	public Vector3d translation();

}
