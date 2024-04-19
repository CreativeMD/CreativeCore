package team.creative.creativecore.common.util.math.box;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.collision.CollisionCoordinator;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class ABB {
    
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
    
    public double get(AABB bb, Facing facing) {
        return switch (facing) {
            case EAST -> bb.maxX;
            case WEST -> bb.minX;
            case UP -> bb.maxY;
            case DOWN -> bb.minY;
            case SOUTH -> bb.maxZ;
            case NORTH -> bb.minZ;
        };
    }
    
    public static ABB createEmptyBox() {
        return new ABB(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }
    
    @Nullable
    public static BlockHitResult clip(Iterable<ABB> boxes, Vec3 pos, Vec3 look, BlockPos blockPos) {
        BlockHitResult hit = null;
        double distance = Double.POSITIVE_INFINITY;
        
        for (ABB box : boxes) {
            BlockHitResult temp = box.rayTrace(pos, look, blockPos);
            
            if (temp != null) {
                double tempDistance = pos.distanceToSqr(temp.getLocation());
                if (tempDistance < distance) {
                    hit = temp;
                    distance = tempDistance;
                }
            }
        }
        return hit;
    }
    
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;
    
    public ABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }
    
    public ABB(AABB bb) {
        this(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }
    
    public ABB(ABB bb) {
        this(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }
    
    public double get(Facing facing) {
        return switch (facing) {
            case EAST -> maxX;
            case WEST -> minX;
            case UP -> maxY;
            case DOWN -> minY;
            case SOUTH -> maxZ;
            case NORTH -> minZ;
        };
    }
    
    public double min(net.minecraft.core.Direction.Axis axis) {
        return switch (axis) {
            case X -> minX;
            case Y -> minY;
            case Z -> minZ;
        };
    }
    
    public double max(net.minecraft.core.Direction.Axis axis) {
        return switch (axis) {
            case X -> maxX;
            case Y -> maxY;
            case Z -> maxZ;
        };
    }
    
    public double min(Axis axis) {
        return switch (axis) {
            case X -> minX;
            case Y -> minY;
            case Z -> minZ;
        };
    }
    
    public double max(Axis axis) {
        return switch (axis) {
            case X -> maxX;
            case Y -> maxY;
            case Z -> maxZ;
        };
    }
    
    public Vec3d corner(BoxCorner corner) {
        return new Vec3d(cornerX(corner), cornerY(corner), cornerZ(corner));
    }
    
    public double cornerValue(AABB bb, BoxCorner corner, team.creative.creativecore.common.util.math.base.Axis axis) {
        return get(corner.getFacing(axis));
    }
    
    public double cornerX(BoxCorner corner) {
        return get(corner.x);
    }
    
    public double cornerY(BoxCorner corner) {
        return get(corner.y);
    }
    
    public double cornerZ(BoxCorner corner) {
        return get(corner.z);
    }
    
    public boolean intersectsWithAxis(Axis axis, AABB bb2) {
        return switch (axis) {
            case X -> minY < bb2.maxY && maxY > bb2.minY && minZ < bb2.maxZ && maxZ > bb2.minZ;
            case Y -> minX < bb2.maxX && maxX > bb2.minX && minZ < bb2.maxZ && maxZ > bb2.minZ;
            case Z -> minX < bb2.maxX && maxX > bb2.minX && minY < bb2.maxY && maxY > bb2.minY;
        };
    }
    
    public boolean intersectsWithAxis(Axis axis, ABB bb2) {
        return switch (axis) {
            case X -> minY < bb2.maxY && maxY > bb2.minY && minZ < bb2.maxZ && maxZ > bb2.minZ;
            case Y -> minX < bb2.maxX && maxX > bb2.minX && minZ < bb2.maxZ && maxZ > bb2.minZ;
            case Z -> minX < bb2.maxX && maxX > bb2.minX && minY < bb2.maxY && maxY > bb2.minY;
        };
    }
    
    public boolean intersectsWithAxis(Axis one, Axis two, double valueOne, double valueTwo) {
        return min(one) < valueOne && max(one) > valueOne && min(two) < valueTwo && max(two) > valueTwo;
    }
    
    public double calculateAxisOffset(Axis axis, Axis one, Axis two, AABB other, double offset) {
        if (intersectsWithAxis(axis, other))
            if (offset > 0.0D && max(other, axis) <= min(axis)) {
                double newDistance = min(axis) - max(other, axis);
                if (newDistance < offset)
                    return newDistance;
            } else if (offset < 0.0D && min(other, axis) >= max(axis)) {
                double newDistance = max(axis) - min(other, axis);
                if (newDistance > offset)
                    return newDistance;
            }
        return offset;
    }
    
    public ABB copy() {
        return new ABB(this);
    }
    
    public void move(double x, double y, double z) {
        minX += x;
        minY += y;
        minZ += z;
        maxX += x;
        maxY += y;
        maxZ += z;
    }
    
    public ABB moveCopy(double x, double y, double z) {
        ABB bb = copy();
        bb.move(x, y, z);
        return bb;
    }
    
    public Vec3d[] getCorners() {
        Vec3d[] corners = new Vec3d[BoxCorner.values().length];
        for (int i = 0; i < corners.length; i++)
            corners[i] = corner(BoxCorner.values()[i]);
        return corners;
    }
    
    public Vec3d[] getRotatedCorners(IVecOrigin origin) {
        Vec3d[] corners = getCorners();
        for (int i = 0; i < corners.length; i++)
            origin.transformPointToWorld(corners[i]);
        return corners;
    }
    
    public Vec3d[] getOuterCorner(Facing facing, IVecOrigin origin, double minOne, double minTwo, double maxOne, double maxTwo) {
        Vec3d[] corners = getCorners();
        
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
                .ordinal()] };
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
            case EAST -> maxX = Math.max(maxX, value);
            case WEST -> minX = Math.min(minX, value);
            case UP -> maxY = Math.max(maxY, value);
            case DOWN -> minY = Math.min(minY, value);
            case SOUTH -> maxZ = Math.max(maxZ, value);
            case NORTH -> minZ = Math.min(minZ, value);
        }
    }
    
    public AABB toVanilla() {
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    public BlockHitResult rayTrace(Vec3 pos, Vec3 look, BlockPos blockPos) {
        double[] time = new double[] { 1.0D };
        double x = look.x - pos.x;
        double y = look.y - pos.y;
        double z = look.z - pos.z;
        
        Facing facing = clipFacing(pos, time, null, x, y, z, blockPos);
        
        if (facing == null)
            return null;
        return new BlockHitResult(pos.add(x * time[0], y * time[0], z * time[0]), facing.toVanilla(), blockPos, false);
    }
    
    private Facing clipFacing(Vec3 pos, double[] time, Facing facing, double x, double y, double z, BlockPos blockPos) {
        for (int i = 0; i < Facing.VALUES.length; i++) {
            Facing toClip = Facing.VALUES[i];
            if (toClip.positive ? toClip.axis.get(x, y, z) < 1.0E-7D : toClip.axis.get(x, y, z) > 1.0E-7D)
                facing = clipPoint(time, facing, toClip, x, y, z, pos, blockPos);
        }
        return facing;
    }
    
    private Facing clipPoint(double[] time, Facing original, Facing toClip, double x, double y, double z, Vec3 pos, BlockPos blockPos) {
        double d0 = (get(toClip) + blockPos.get(toClip.axis.toVanilla()) - pos.get(toClip.axis.toVanilla())) / toClip.axis.get(x, y, z);
        double d1 = pos.get(toClip.one().toVanilla()) + d0 * toClip.one().get(x, y, z);
        double d2 = pos.get(toClip.two().toVanilla()) + d0 * toClip.two().get(x, y, z);
        if (0.0D < d0 && d0 < time[0] && min(toClip.one()) + blockPos.get(toClip.one().toVanilla()) - 1.0E-7D < d1 && d1 < max(toClip.one()) + blockPos
                .get(toClip.one().toVanilla()) + 1.0E-7D && min(toClip.two()) + blockPos.get(toClip.two().toVanilla()) - 1.0E-7D < d2 && d2 < max(toClip.two()) + blockPos
                        .get(toClip.two().toVanilla()) + 1.0E-7D) {
            time[0] = d0;
            return toClip;
        }
        return original;
    }
    
    public ABB createRotatedSurrounding(CollisionCoordinator coordinator) {
        Vec3d[] corners = getRotatedCorners(coordinator.original());
        
        ABB bb = createEmptyBox();
        
        for (int i = 0; i < corners.length; i++) {
            Vec3d vec = corners[i];
            
            bb.include(vec);
            
            if (coordinator.hasOnlyTranslation()) {
                vec.add(coordinator.translation);
                bb.include(vec);
            } else {
                BoxUtils.includeMaxRotationInBox(bb, new Vec3d(vec), Axis.X, coordinator);
                BoxUtils.includeMaxRotationInBox(bb, new Vec3d(vec), Axis.Y, coordinator);
                BoxUtils.includeMaxRotationInBox(bb, new Vec3d(vec), Axis.Z, coordinator);
                
                coordinator.transform(vec, 1D);
                bb.include(vec);
            }
        }
        
        return bb;
    }
    
    /** the resulting box is still an obb (orientated to origin of the coordinator */
    public ABB createRotatedSurroundingInverseInternal(CollisionCoordinator coordinator) {
        Vec3d[] corners = getCorners();
        
        ABB bb = createEmptyBox();
        
        for (int i = 0; i < corners.length; i++) {
            Vec3d vec = corners[i];
            
            bb.include(vec);
            
            if (coordinator.hasOnlyTranslation()) {
                vec.sub(coordinator.translation);
                bb.include(vec);
            } else {
                BoxUtils.includeMaxRotationInBoxInverse(bb, new Vec3d(vec), Axis.X, coordinator);
                BoxUtils.includeMaxRotationInBoxInverse(bb, new Vec3d(vec), Axis.Y, coordinator);
                BoxUtils.includeMaxRotationInBoxInverse(bb, new Vec3d(vec), Axis.Z, coordinator);
                
                coordinator.transformInverted(vec, 1D);
                bb.include(vec);
            }
        }
        
        return bb;
    }
    
    public boolean contains(Vec3d vec) {
        return this.contains(vec.x, vec.y, vec.z);
    }
    
    public boolean contains(Vec3 vec) {
        return this.contains(vec.x, vec.y, vec.z);
    }
    
    public boolean intersectsPrecise(AABB bb) {
        return this.intersects(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }
    
    public boolean intersects(AABB bb) {
        return this.intersects(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }
    
    public boolean intersects(ABB bb) {
        return this.intersects(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }
    
    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
    }
    
    public boolean intersects(Vec3 vec1, Vec3 vec2) {
        return this.intersects(Math.min(vec1.x, vec2.x), Math.min(vec1.y, vec2.y), Math.min(vec1.z, vec2.z), Math.max(vec1.x, vec2.x), Math.max(vec1.y, vec2.y), Math
                .max(vec1.z, vec2.z));
    }
    
    public boolean contains(double x, double y, double z) {
        return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
    }
    
    public Vec3 getCenter() {
        return new Vec3(Mth.lerp(0.5D, this.minX, this.maxX), Mth.lerp(0.5D, this.minY, this.maxY), Mth.lerp(0.5D, this.minZ, this.maxZ));
    }
    
    @Override
    public String toString() {
        return "ABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
    
}
