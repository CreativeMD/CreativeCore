package com.creativemd.creativecore.common.utils.math.vec;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class VecOrigin implements IVecOrigin {
	
	public VecOrigin(Vector3d center) {
		this.center = center;
	}
	
	protected boolean rotated = false;
	
	private Vector3d center;
	private Vector3d translation = new Vector3d(0, 0, 0);
	private Matrix3d rotation = MatrixUtils.createIdentityMatrix();
	private Matrix3d rotationInv = rotation;
	
	protected double rotX;
	protected double rotY;
	protected double rotZ;
	
	protected double offsetX;
	protected double offsetY;
	protected double offsetZ;
	
	@Override
	public double offX() {
		return offsetX;
	}
	
	@Override
	public double offY() {
		return offsetY;
	}
	
	@Override
	public double offZ() {
		return offsetZ;
	}
	
	@Override
	public double rotX() {
		return rotX;
	}
	
	@Override
	public double rotY() {
		return rotY;
	}
	
	@Override
	public double rotZ() {
		return rotZ;
	}
	
	@Override
	public boolean isRotated() {
		return rotated;
	}
	
	protected void updateRotated() {
		rotated = rotX % 360 != 0 || rotY % 360 != 0 || rotZ % 360 != 0;
		rotation = MatrixUtils.createRotationMatrix(rotX, rotY, rotZ);
		rotationInv.invert(rotation);
	}
	
	protected void updateTranslation() {
		translation.set(offsetX, offsetY, offsetZ);
	}
	
	@Override
	public void offX(double value) {
		this.offsetX = value;
		updateTranslation();
	}
	
	@Override
	public void offY(double value) {
		this.offsetY = value;
		updateTranslation();
	}
	
	@Override
	public void offZ(double value) {
		this.offsetZ = value;
		updateTranslation();
	}
	
	@Override
	public void off(double x, double y, double z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		updateTranslation();
	}
	
	@Override
	public void rotX(double value) {
		this.rotX = value;
		updateRotated();
	}
	
	@Override
	public void rotY(double value) {
		this.rotY = value;
		updateRotated();
	}
	
	@Override
	public void rotZ(double value) {
		this.rotZ = value;
		updateRotated();
	}
	
	@Override
	public void rot(double x, double y, double z) {
		this.rotX = x;
		this.rotY = y;
		this.rotZ = z;
		updateRotated();
	}
	
	@Override
	public Vector3d center() {
		return center;
	}
	
	@Override
	public Matrix3d rotation() {
		return rotation;
	}
	
	@Override
	public Matrix3d rotationInv() {
		return rotationInv;
	}
	
	@Override
	public Vector3d translation() {
		return translation;
	}
	
	@Override
	public void setCenter(Vector3d vec) {
		this.center.set(vec);
	}
	
}
