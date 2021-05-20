package com.creativemd.creativecore.common.utils.math;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

import com.creativemd.creativecore.common.utils.math.vec.VectorFan;

import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class VectorUtils {
    
    public static void set(Tuple3d vec, double value, Axis axis) {
        switch (axis) {
        case X:
            vec.x = value;
            break;
        case Y:
            vec.y = value;
            break;
        case Z:
            vec.z = value;
            break;
        }
    }
    
    public static void set(Tuple3f vec, float value, Axis axis) {
        switch (axis) {
        case X:
            vec.x = value;
            break;
        case Y:
            vec.y = value;
            break;
        case Z:
            vec.z = value;
            break;
        }
    }
    
    public static Vec3d set(Vec3d vec, double value, Axis axis) {
        switch (axis) {
        case X:
            return new Vec3d(value, vec.y, vec.z);
        case Y:
            return new Vec3d(vec.x, value, vec.z);
        case Z:
            return new Vec3d(vec.x, vec.y, value);
        }
        return null;
    }
    
    public static BlockPos set(BlockPos vec, int value, Axis axis) {
        switch (axis) {
        case X:
            return new BlockPos(value, vec.getY(), vec.getZ());
        case Y:
            return new BlockPos(vec.getX(), value, vec.getZ());
        case Z:
            return new BlockPos(vec.getX(), vec.getY(), value);
        }
        return null;
    }
    
    public static double get(Axis axis, Tuple3d vec) {
        return get(axis, vec.x, vec.y, vec.z);
    }
    
    public static float get(Axis axis, Tuple3f vec) {
        return get(axis, vec.x, vec.y, vec.z);
    }
    
    public static double get(Axis axis, Vec3d vec) {
        return get(axis, vec.x, vec.y, vec.z);
    }
    
    public static int get(Axis axis, Vec3i vec) {
        return get(axis, vec.getX(), vec.getY(), vec.getZ());
    }
    
    public static float get(Axis axis, float x, float y, float z) {
        switch (axis) {
        case X:
            return x;
        case Y:
            return y;
        case Z:
            return z;
        }
        return 0;
    }
    
    public static double get(Axis axis, double x, double y, double z) {
        switch (axis) {
        case X:
            return x;
        case Y:
            return y;
        case Z:
            return z;
        }
        return 0;
    }
    
    public static int get(Axis axis, int x, int y, int z) {
        switch (axis) {
        case X:
            return x;
        case Y:
            return y;
        case Z:
            return z;
        }
        return 0;
    }
    
    public static boolean isZero(double number) {
        return number > -VectorFan.EPSILON && number < VectorFan.EPSILON;
    }
    
    public static boolean isZero(float number) {
        return number > -VectorFan.EPSILON && number < VectorFan.EPSILON;
    }
    
    public static boolean equals(double number, double number2) {
        return number - number2 > -VectorFan.EPSILON && number - number2 < VectorFan.EPSILON;
    }
    
    public static boolean equals(float number, float number2) {
        return number - number2 > -VectorFan.EPSILON && number - number2 < VectorFan.EPSILON;
    }
    
}
