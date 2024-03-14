package team.creative.creativecore.common.util.math.box;

import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.collision.CollisionCoordinator;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.matrix.Matrix3;
import team.creative.creativecore.common.util.math.transformation.BooleanRotation;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class BoxUtils {
    
    public static boolean equals(double a, double b, double deviation) {
        return a == b || Math.abs(a - b) < deviation;
    }
    
    public static boolean greaterEquals(double a, double b, double deviation) {
        return a >= (b > 0 ? b - deviation : b + deviation);
    }
    
    public static boolean insideRect(double one, double two, double minOne, double minTwo, double maxOne, double maxTwo) {
        return one > minOne && one < maxOne && two > minTwo && two < maxTwo;
    }
    
    private static double lengthIgnoreAxis(Vec3d vec, Axis axis, Vec3d center) {
        vec.sub(center);
        double result = switch (axis) {
            case X -> Math.sqrt(vec.y * vec.y + vec.z * vec.z);
            case Y -> Math.sqrt(vec.x * vec.x + vec.z * vec.z);
            case Z -> Math.sqrt(vec.x * vec.x + vec.y * vec.y);
            default -> 0;
        };
        vec.add(center);
        return result;
    }
    
    public static void includeMaxRotationInBox(ABB box, Vec3d vec, Axis axis, CollisionCoordinator coordinator) {
        double rotation = coordinator.getRotationDegree(axis);
        if (rotation == 0)
            return;
        includeMaxRotationInBox(box, vec, axis, rotation, coordinator.original().center(), coordinator.getRotationMatrix(axis), coordinator.translation);
    }
    
    public static void includeMaxRotationInBoxInverse(ABB box, Vec3d vec, Axis axis, CollisionCoordinator coordinator) {
        double rotation = -coordinator.getRotationDegree(axis);
        if (rotation == 0)
            return;
        Vec3d translation;
        if (coordinator.translation != null) {
            translation = new Vec3d(coordinator.translation);
            translation.invert();
        } else
            translation = null;
        includeMaxRotationInBox(box, vec, axis, rotation, coordinator.original().center(), coordinator.getRotationMatrixInv(axis), translation);
    }
    
    private static void includeMaxRotationInBox(ABB box, Vec3d vec, Axis axis, double rotation, Vec3d rotationCenter, Matrix3 matrix, Vec3d translation) {
        Double length = null;
        BooleanRotation state = BooleanRotation.get(axis, vec);
        
        boolean positive = rotation > 0;
        int quarterRotation = 90;
        
        if (rotation >= 90) {
            while (quarterRotation <= Math.abs(rotation) && quarterRotation < 360) {
                Facing facing = state.clockwiseMaxFacing();
                
                if (length == null)
                    length = lengthIgnoreAxis(vec, axis, rotationCenter);
                
                box.include(facing, length);
                if (translation != null)
                    box.include(facing, length + translation.get(facing.axis));
                
                state = state.clockwise();
                quarterRotation += 90;
            }
        }
        
        vec.sub(rotationCenter);
        matrix.transform(vec);
        vec.add(rotationCenter);
        
        box.include(vec);
        
        if (quarterRotation <= 360 && state != null && !state.is(vec)) {
            Facing facing = positive ? state.clockwiseMaxFacing() : state.counterMaxClockwiseFacing();
            
            if (length == null)
                length = lengthIgnoreAxis(vec, axis, rotationCenter);
            
            box.include(facing, length);
            if (translation != null)
                box.include(facing, length + translation.get(facing.axis));
        }
    }
    
    public static double get(AABB bb, Facing facing) {
        return switch (facing) {
            case EAST -> bb.maxX;
            case WEST -> bb.minX;
            case UP -> bb.maxY;
            case DOWN -> bb.minY;
            case SOUTH -> bb.maxZ;
            case NORTH -> bb.minZ;
        };
    }
    
    public static double min(AABB bb, net.minecraft.core.Direction.Axis axis) {
        return switch (axis) {
            case X -> bb.minX;
            case Y -> bb.minY;
            case Z -> bb.minZ;
        };
    }
    
    public static double max(AABB bb, net.minecraft.core.Direction.Axis axis) {
        return switch (axis) {
            case X -> bb.maxX;
            case Y -> bb.maxY;
            case Z -> bb.maxZ;
        };
    }
    
    public static double min(AABB bb, Axis axis) {
        return switch (axis) {
            case X -> bb.minX;
            case Y -> bb.minY;
            case Z -> bb.minZ;
        };
    }
    
    public static double max(AABB bb, Axis axis) {
        return switch (axis) {
            case X -> bb.maxX;
            case Y -> bb.maxY;
            case Z -> bb.maxZ;
        };
    }
    
    public static Vec3d corner(AABB bb, BoxCorner corner) {
        return new Vec3d(cornerX(bb, corner), cornerY(bb, corner), cornerZ(bb, corner));
    }
    
    public static double cornerValue(AABB bb, BoxCorner corner, Axis axis) {
        return get(bb, corner.getFacing(axis));
    }
    
    public static double cornerX(AABB bb, BoxCorner corner) {
        return get(bb, corner.x);
    }
    
    public static double cornerY(AABB bb, BoxCorner corner) {
        return get(bb, corner.y);
    }
    
    public static double cornerZ(AABB bb, BoxCorner corner) {
        return get(bb, corner.z);
    }
    
    public static Vec3d[] getCorners(AABB bb) {
        Vec3d[] corners = new Vec3d[BoxCorner.values().length];
        for (int i = 0; i < corners.length; i++)
            corners[i] = corner(bb, BoxCorner.values()[i]);
        return corners;
    }
    
    public static Vec3d[] getRotatedCorners(AABB bb, IVecOrigin origin) {
        Vec3d[] corners = getCorners(bb);
        for (int i = 0; i < corners.length; i++)
            origin.transformPointToWorld(corners[i]);
        return corners;
    }
    
    public static boolean intersectsWithAxis(AABB bb, AABB other, Axis one, Axis two) {
        return bb.min(one.toVanilla()) < other.max(one.toVanilla()) && bb.max(one.toVanilla()) > bb.min(one.toVanilla()) && bb.min(two.toVanilla()) < bb.max(two.toVanilla()) && bb
                .max(two.toVanilla()) > bb.min(two.toVanilla());
    }
    
    public static boolean intersectsWithAxis(AABB bb, Axis one, Axis two, double valueOne, double valueTwo) {
        return bb.min(one.toVanilla()) < valueOne && bb.max(one.toVanilla()) > valueOne && bb.min(two.toVanilla()) < valueTwo && bb.max(two.toVanilla()) > valueTwo;
    }
    
    public static double calculateAxisOffset(Axis axis, Axis one, Axis two, AABB bb, AABB other, double offset) {
        if (intersectsWithAxis(bb, other, one, two))
            if (offset > 0.0D && other.max(axis.toVanilla()) <= bb.min(axis.toVanilla())) {
                double newDistance = bb.min(axis.toVanilla()) - other.maxX;
                if (newDistance < offset)
                    return newDistance;
            } else if (offset < 0.0D && other.min(axis.toVanilla()) >= bb.max(axis.toVanilla())) {
                double newDistance = bb.max(axis.toVanilla()) - other.min(axis.toVanilla());
                if (newDistance > offset)
                    return newDistance;
            }
        return offset;
    }
    
    public static double getIntersectionVolume(AABB bb, ABB other) {
        double d0 = Math.max(bb.minX, other.minX);
        double d1 = Math.max(bb.minY, other.minY);
        double d2 = Math.max(bb.minZ, other.minZ);
        double d3 = Math.min(bb.maxX, other.maxX);
        double d4 = Math.min(bb.maxY, other.maxY);
        double d5 = Math.min(bb.maxZ, other.maxZ);
        if (d0 < d3 && d1 < d4 && d2 < d5)
            return Math.abs((d3 - d0) * (d4 - d1) * (d5 - d2));
        return 0;
    }
}
