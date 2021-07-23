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
        return a == b ? true : Math.abs(a - b) < deviation;
    }
    
    public static boolean greaterEquals(double a, double b, double deviation) {
        return a >= (b > 0 ? b - deviation : b + deviation);
    }
    
    public static boolean insideRect(double one, double two, double minOne, double minTwo, double maxOne, double maxTwo) {
        return one > minOne && one < maxOne && two > minTwo && two < maxTwo;
    }
    
    /*public static Vec3d[] getCorners(AxisAlignedBB box) {
        Vec3d[] corners = new Vec3d[BoxCorner.values().length];
        for (int i = 0; i < corners.length; i++)
            corners[i] = BoxCorner.values()[i].getVector(box);
        return corners;
    }*/
    
    private static double lengthIgnoreAxis(Vec3d vec, Axis axis) {
        switch (axis) {
        case X:
            return Math.sqrt(vec.y * vec.y + vec.z * vec.z);
        case Y:
            return Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        case Z:
            return Math.sqrt(vec.x * vec.x + vec.y * vec.y);
        default:
            return 0;
        }
    }
    
    private static void includeMaxRotationInBox(IncludeBox box, Vec3d vec, Axis axis, CollisionCoordinator coordinator) {
        double rotation = coordinator.getRotationDegree(axis);
        if (rotation == 0)
            return;
        
        Matrix3 matrix = coordinator.getRotationMatrix(axis);
        Double length = null;
        BooleanRotation state = BooleanRotation.get(axis, vec);
        
        boolean positive = rotation > 0;
        int quarterRotation = 90;
        
        if (rotation >= 90) {
            while (quarterRotation <= Math.abs(rotation) && quarterRotation < 360) {
                Facing facing = positive ? state.clockwiseMaxFacing() : state.counterMaxClockwiseFacing();
                
                if (length == null)
                    length = lengthIgnoreAxis(vec, axis);
                
                box.include(facing, length);
                if (coordinator.translation != null)
                    box.include(facing, length + coordinator.translation.get(facing.axis));
                
                state = state.clockwise();
                quarterRotation += 90;
            }
        }
        
        matrix.transform(vec);
        box.include(vec);
        
        if (quarterRotation <= 360 && !state.is(vec)) {
            Facing facing = positive ? state.clockwiseMaxFacing() : state.counterMaxClockwiseFacing();
            
            if (length == null)
                length = lengthIgnoreAxis(vec, axis);
            
            box.include(facing, length);
            if (coordinator.translation != null)
                box.include(facing, length + coordinator.translation.get(facing.axis));
        }
    }
    
    public static AABB getRotatedSurrounding(AABB boundingBox, CollisionCoordinator coordinator) {
        Vec3d[] corners = getRotatedCorners(boundingBox, coordinator.origin);
        
        IncludeBox bb = new IncludeBox();
        
        for (int i = 0; i < corners.length; i++) {
            Vec3d vec = corners[i];
            
            bb.include(vec);
            
            if (coordinator.hasOnlyTranslation()) {
                vec.add(coordinator.translation);
                bb.include(vec);
            } else {
                includeMaxRotationInBox(bb, new Vec3d(vec), Axis.X, coordinator);
                includeMaxRotationInBox(bb, new Vec3d(vec), Axis.Y, coordinator);
                includeMaxRotationInBox(bb, new Vec3d(vec), Axis.Z, coordinator);
                
                coordinator.transform(vec, 1D);
                bb.include(vec);
            }
        }
        
        return bb.getAxisBB();
    }
    
    public static Vec3d[] getRotatedCorners(AABB box, IVecOrigin origin) {
        /*Vec3d[] corners = getCorners(box); TODO
        for (int i = 0; i < corners.length; i++) {
            Vec3d vec = corners[i];
            origin.transformPointToWorld(vec);
        }
        return corners;*/
        return null;
    }
    
    public static Vec3d[] getOuterCorner(Facing facing, IVecOrigin origin, AABB box, double minOne, double minTwo, double maxOne, double maxTwo) {
        /*Vec3d[] corners = getCorners(box); TODO
        
        double value = 0;
        BoxCorner selected = null;
        Axis axis = facing.axis;
        
        for (int i = 0; i < corners.length; i++) {
            Vec3d vec = corners[i];
            origin.transformPointToWorld(vec);
            
            double vectorValue = vec.get(axis);
            if (selected == null || (facing.positive ? vectorValue > value : vectorValue < value)) {
                selected = BoxCorner.values()[i];
                value = vectorValue;
            }
        }
        
        return new Vec3d[] { corners[selected.ordinal()], corners[selected.neighborOne.ordinal()], corners[selected.neighborTwo.ordinal()], corners[selected.neighborThree
                .ordinal()] };*/
        return null;
    }
    
    private static class IncludeBox {
        
        public double minX;
        public double minY;
        public double minZ;
        public double maxX;
        public double maxY;
        public double maxZ;
        
        public IncludeBox() {
            minX = Double.MAX_VALUE;
            minY = Double.MAX_VALUE;
            minZ = Double.MAX_VALUE;
            maxX = -Double.MAX_VALUE;
            maxY = -Double.MAX_VALUE;
            maxZ = -Double.MAX_VALUE;
        }
        
        public void include(Vec3d vec) {
            minX = Math.min(minX, vec.x);
            minY = Math.min(minY, vec.y);
            minZ = Math.min(minZ, vec.z);
            maxX = Math.max(maxX, vec.x);
            maxY = Math.max(maxY, vec.y);
            maxZ = Math.max(maxZ, vec.z);
        }
        
        public void include(Facing facing, double value) {
            switch (facing) {
            case EAST:
                maxX = Math.max(maxX, value);
                break;
            case WEST:
                minX = Math.min(minX, value);
                break;
            case UP:
                maxY = Math.max(maxY, value);
                break;
            case DOWN:
                minY = Math.min(minY, value);
                break;
            case SOUTH:
                maxZ = Math.max(maxZ, value);
                break;
            case NORTH:
                minZ = Math.min(minZ, value);
                break;
            }
        }
        
        public AABB getAxisBB() {
            return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        }
        
    }
    
}
