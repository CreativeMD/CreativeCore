package com.creativemd.creativecore.common.utils.math.vec;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.box.BoxUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils.BoxCorner;
import com.creativemd.creativecore.common.utils.math.box.OrientatedBoundingBox;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

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
	
	public Vector3d center();
	
	public void setCenter(Vector3d vec);
	
	public Matrix3d rotation();
	
	public Matrix3d rotationInv();
	
	public Vector3d translation();
	
	public default void transformPointToWorld(Vector3d vec) {
		vec.sub(center());
		rotation().transform(vec);
		vec.add(center());
		
		vec.add(translation());
	}
	
	public default void transformPointToFakeWorld(Vector3d vec) {
		vec.sub(translation());
		
		vec.sub(center());
		rotationInv().transform(vec);
		vec.add(center());
	}
	
	public default Vec3d transformPointToWorld(Vec3d vec) {
		Vector3d real = new Vector3d(vec.x, vec.y, vec.z);
		transformPointToWorld(real);
		return new Vec3d(real.x, real.y, real.z);
	}
	
	public default Vec3d transformPointToFakeWorld(Vec3d vec) {
		Vector3d real = new Vector3d(vec.x, vec.y, vec.z);
		transformPointToFakeWorld(real);
		return new Vec3d(real.x, real.y, real.z);
	}
	
	public default AxisAlignedBB getAxisAlignedBox(AxisAlignedBB box) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (int i = 0; i < BoxUtils.BoxCorner.values().length; i++) {
			Vector3d vec = BoxCorner.values()[i].getVector(box);
			
			transformPointToWorld(vec);
			
			minX = Math.min(minX, vec.x);
			minY = Math.min(minY, vec.y);
			minZ = Math.min(minZ, vec.z);
			maxX = Math.max(maxX, vec.x);
			maxY = Math.max(maxY, vec.y);
			maxZ = Math.max(maxZ, vec.z);
		}
		
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public default OrientatedBoundingBox getOrientatedBox(AxisAlignedBB box) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (int i = 0; i < BoxUtils.BoxCorner.values().length; i++) {
			Vector3d vec = BoxCorner.values()[i].getVector(box);
			
			transformPointToFakeWorld(vec);
			
			minX = Math.min(minX, vec.x);
			minY = Math.min(minY, vec.y);
			minZ = Math.min(minZ, vec.z);
			maxX = Math.max(maxX, vec.x);
			maxY = Math.max(maxY, vec.y);
			maxZ = Math.max(maxZ, vec.z);
		}
		
		return new OrientatedBoundingBox(this, minX, minY, minZ, maxX, maxY, maxZ);
	}
	
}
