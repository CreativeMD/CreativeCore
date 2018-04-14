package com.creativemd.creativecore.common.utils;

import javax.vecmath.Vector3d;

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
	
}
