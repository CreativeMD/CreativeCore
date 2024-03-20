package team.creative.creativecore.common.util.math.vec;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.geo.VectorFan;

public class VectorUtils {
    
    public static Vector3d set(Vector3d vec, double value, Axis axis) {
        switch (axis) {
            case X -> new Vector3d(value, vec.y, vec.z);
            case Y -> new Vector3d(vec.x, value, vec.z);
            case Z -> new Vector3d(vec.x, vec.y, value);
        }
        throw new IllegalArgumentException();
    }
    
    public static Vec3 set(Vec3 vec, double value, Axis axis) {
        return switch (axis) {
            case X -> new Vec3(value, vec.y, vec.z);
            case Y -> new Vec3(vec.x, value, vec.z);
            case Z -> new Vec3(vec.x, vec.y, value);
        };
    }
    
    public static void set(MutableBlockPos vec, int value, Axis axis) {
        switch (axis) {
            case X:
                vec.setX(value);
                break;
            case Y:
                vec.setY(value);
                break;
            case Z:
                vec.setZ(value);
                break;
        }
        throw new IllegalArgumentException();
    }
    
    public static BlockPos set(BlockPos vec, int value, Axis axis) {
        return switch (axis) {
            case X -> new BlockPos(value, vec.getY(), vec.getZ());
            case Y -> new BlockPos(vec.getX(), value, vec.getZ());
            case Z -> new BlockPos(vec.getX(), vec.getY(), value);
        };
    }
    
    public static double get(Axis axis, Vector3d vec) {
        return get(axis, vec.x, vec.y, vec.z);
    }
    
    public static double get(Axis axis, Vec3 vec) {
        return get(axis, vec.x, vec.y, vec.z);
    }
    
    public static double get(net.minecraft.core.Direction.Axis axis, Vec3 vec) {
        return get(axis, vec.x, vec.y, vec.z);
    }
    
    public static float get(Axis axis, Vector3f vec) {
        return get(axis, vec.x(), vec.y(), vec.z());
    }
    
    public static int get(Axis axis, Vec3i vec) {
        return get(axis, vec.getX(), vec.getY(), vec.getZ());
    }
    
    public static float get(Axis axis, float x, float y, float z) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
        };
    }
    
    public static double get(Axis axis, double x, double y, double z) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
        };
    }
    
    public static int get(Axis axis, int x, int y, int z) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
        };
    }
    
    public static float get(net.minecraft.core.Direction.Axis axis, float x, float y, float z) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
        };
    }
    
    public static double get(net.minecraft.core.Direction.Axis axis, double x, double y, double z) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
        };
    }
    
    public static int get(net.minecraft.core.Direction.Axis axis, int x, int y, int z) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
        };
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
    
    public static boolean greaterEquals(float number, float number2) {
        return number > number2 || (number - number2 > -VectorFan.EPSILON && number - number2 < VectorFan.EPSILON);
    }
    
    public static boolean smallerEquals(float number, float number2) {
        return number < number2 || (number - number2 > -VectorFan.EPSILON && number - number2 < VectorFan.EPSILON);
    }
    
    public static boolean greaterEquals(double number, double number2) {
        return number > number2 || (number - number2 > -VectorFan.EPSILON && number - number2 < VectorFan.EPSILON);
    }
    
    public static boolean smallerEquals(double number, double number2) {
        return number < number2 || (number - number2 > -VectorFan.EPSILON && number - number2 < VectorFan.EPSILON);
    }
    
}
