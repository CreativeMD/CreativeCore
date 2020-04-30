package com.creativemd.creativecore.common.utils.math.geo;

import javax.vecmath.Vector3f;

import com.creativemd.creativecore.common.utils.math.RotationUtils;

import net.minecraft.util.EnumFacing;

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
		RotationUtils.setValue(direction, facing.getAxisDirection().getOffset(), facing.getAxis());
	}
	
}
