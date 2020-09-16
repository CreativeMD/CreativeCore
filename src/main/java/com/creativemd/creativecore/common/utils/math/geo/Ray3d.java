package com.creativemd.creativecore.common.utils.math.geo;

import javax.vecmath.Vector3f;

import com.creativemd.creativecore.common.utils.math.VectorUtils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public class Ray3d {
	
	public final Vector3f origin;
	public final Vector3f direction;
	
	public Ray3d(Vector3f start, Vector3f end) {
		this.origin = start;
		this.direction = new Vector3f(end);
		this.direction.sub(origin);
		this.direction.normalize();
	}
	
	public Ray3d(Vector3f origin, EnumFacing facing) {
		this.origin = origin;
		this.direction = new Vector3f();
		VectorUtils.set(direction, facing.getAxisDirection().getOffset(), facing.getAxis());
	}
	
	public void set(float x, float y, float z, float x2, float y2, float z2) {
		origin.set(x, y, z);
		direction.set(x2, y2, z2);
		direction.sub(origin);
	}
	
	public double getT(Axis axis, double value) {
		return (value - VectorUtils.get(axis, origin)) / VectorUtils.get(axis, direction);
	}
	
	public Vector3f get(float t) {
		return new Vector3f(origin.x + direction.x * t, origin.y + direction.y * t, origin.z + direction.z * t);
	}
	
}
