package com.creativemd.creativecore.common.utils.math.geo;

import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.VectorUtils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public class Ray3d {
    
    public final Vector3d origin;
    public final Vector3d direction;
    
    public Ray3d(Vector3d start, Vector3d end) {
        this.origin = start;
        this.direction = new Vector3d(end);
        this.direction.sub(origin);
        this.direction.normalize();
    }
    
    public Ray3d(Vector3d start, Vector3d end, boolean normalize) {
        this.origin = start;
        this.direction = new Vector3d(end);
        this.direction.sub(origin);
        if (normalize)
            this.direction.normalize();
    }
    
    public Ray3d(Vector3d origin, EnumFacing facing) {
        this.origin = origin;
        this.direction = new Vector3d();
        VectorUtils.set(direction, facing.getAxisDirection().getOffset(), facing.getAxis());
    }
    
    public void set(double x, double y, double z, double x2, double y2, double z2) {
        origin.set(x, y, z);
        direction.set(x2, y2, z2);
        direction.sub(origin);
    }
    
    public double getT(Axis axis, double value) {
        return (value - VectorUtils.get(axis, origin)) / VectorUtils.get(axis, direction);
    }
    
    public Vector3d get(float t) {
        return new Vector3d(origin.x + direction.x * t, origin.y + direction.y * t, origin.z + direction.z * t);
    }
    
}
