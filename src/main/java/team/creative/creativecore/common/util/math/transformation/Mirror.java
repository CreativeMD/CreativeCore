package team.creative.creativecore.common.util.math.transformation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public class Mirror {
    
    public static Direction mirror(Direction facing, Axis axis) {
        if (facing.getAxis() == axis)
            return facing.getOpposite();
        return facing;
    }
    
    public static Vec3i mirror(Vec3i vec, Axis axis) {
        switch (axis) {
        case X:
            return new Vec3i(-vec.getX(), vec.getY(), vec.getZ());
        case Y:
            return new Vec3i(vec.getX(), -vec.getY(), vec.getZ());
        case Z:
            return new Vec3i(vec.getX(), vec.getY(), -vec.getZ());
        }
        return vec;
    }
    
    public static BlockPos mirror(BlockPos vec, Axis axis) {
        switch (axis) {
        case X:
            return new BlockPos(-vec.getX(), vec.getY(), vec.getZ());
        case Y:
            return new BlockPos(vec.getX(), -vec.getY(), vec.getZ());
        case Z:
            return new BlockPos(vec.getX(), vec.getY(), -vec.getZ());
        }
        return vec;
    }
    
    public static void mirror(Vec3d vec, Axis axis) {
        switch (axis) {
        case X:
            vec.x = -vec.x;
            break;
        case Y:
            vec.y = -vec.y;
            break;
        case Z:
            vec.z = -vec.z;
            break;
        }
    }
    
    public static void mirror(Vec3f vec, Axis axis) {
        switch (axis) {
        case X:
            vec.x = -vec.x;
            break;
        case Y:
            vec.y = -vec.y;
            break;
        case Z:
            vec.z = -vec.z;
            break;
        }
    }
}
