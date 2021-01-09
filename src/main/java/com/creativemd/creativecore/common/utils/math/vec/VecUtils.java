package com.creativemd.creativecore.common.utils.math.vec;

import javax.vecmath.Vector3d;

public class VecUtils {
    
    public static double distanceToSquared(double x, double y, double z, Vector3d to) {
        return (x - to.x) * (x - to.x) + (y - to.y) * (y - to.y) + (z - to.z) * (z - to.z);
    }
    
    public static double distanceTo(double x, double y, double z, Vector3d to) {
        return Math.sqrt((x - to.x) * (x - to.x) + (y - to.y) * (y - to.y) + (z - to.z) * (z - to.z));
    }
    
    public static double distanceToSquared(Vector3d from, Vector3d to) {
        return (from.x - to.x) * (from.x - to.x) + (from.y - to.y) * (from.y - to.y) + (from.z - to.z) * (from.z - to.z);
    }
    
    public static double distanceTo(Vector3d from, Vector3d to) {
        return Math.sqrt((from.x - to.x) * (from.x - to.x) + (from.y - to.y) * (from.y - to.y) + (from.z - to.z) * (from.z - to.z));
    }
    
}
