package team.creative.creativecore.common.util.math.box;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.type.list.SingletonList;
import team.creative.creativecore.common.util.unsafe.CreativeHackery;

public class AABBVoxelShape extends SliceShape {
    
    public static AABBVoxelShape create(AABB bb) {
        AABBVoxelShape shape = CreativeHackery.allocateInstance(AABBVoxelShape.class);
        shape.bb = bb;
        return shape;
    }
    
    public AABB bb;
    
    protected AABBVoxelShape() {
        super(null, null, 0);
    }
    
    public boolean contains(Vec3d vec) {
        if (vec.x > bb.minX && vec.x < bb.maxX) {
            if (vec.y > bb.minY && vec.y < bb.maxY) {
                return vec.z > bb.minZ && vec.z < bb.maxZ;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    protected double get(Facing facing) {
        switch (facing) {
            case EAST:
                return bb.maxX;
            case WEST:
                return bb.minX;
            case UP:
                return bb.maxY;
            case DOWN:
                return bb.minY;
            case SOUTH:
                return bb.maxZ;
            case NORTH:
                return bb.minZ;
            
        }
        return 0;
    }
    
    public Vec3d getCorner(BoxCorner corner) {
        return new Vec3d(getCornerX(corner), getCornerY(corner), getCornerZ(corner));
    }
    
    public double getCornerValue(BoxCorner corner, team.creative.creativecore.common.util.math.base.Axis axis) {
        return get(corner.getFacing(axis));
    }
    
    public double getCornerX(BoxCorner corner) {
        return get(corner.x);
    }
    
    public double getCornerY(BoxCorner corner) {
        return get(corner.y);
    }
    
    public double getCornerZ(BoxCorner corner) {
        return get(corner.z);
    }
    
    public Vec3d getSizeVec() {
        return new Vec3d(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ);
    }
    
    public double getVolume() {
        return (bb.maxX - bb.minX) * (bb.maxY - bb.minY) * (bb.maxZ - bb.minZ);
    }
    
    public double getIntersectionVolume(AABB other) {
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
    
    public double getSize(Axis axis) {
        switch (axis) {
            case X:
                return bb.maxX - bb.minX;
            case Y:
                return bb.maxY - bb.minY;
            case Z:
                return bb.maxZ - bb.minZ;
        }
        return 0;
    }
    
    public double getMin(Axis axis) {
        switch (axis) {
            case X:
                return bb.minX;
            case Y:
                return bb.minY;
            case Z:
                return bb.minZ;
        }
        return 0;
    }
    
    public double getMax(Axis axis) {
        switch (axis) {
            case X:
                return bb.maxX;
            case Y:
                return bb.maxY;
            case Z:
                return bb.maxZ;
        }
        return 0;
    }
    
    public double getMin(team.creative.creativecore.common.util.math.base.Axis axis) {
        switch (axis) {
            case X:
                return bb.minX;
            case Y:
                return bb.minY;
            case Z:
                return bb.minZ;
        }
        return 0;
    }
    
    public double getMax(team.creative.creativecore.common.util.math.base.Axis axis) {
        switch (axis) {
            case X:
                return bb.maxX;
            case Y:
                return bb.maxY;
            case Z:
                return bb.maxZ;
        }
        return 0;
    }
    
    public static double get(AABB bb, Facing facing) {
        switch (facing) {
            case EAST:
                return bb.maxX;
            case WEST:
                return bb.minX;
            case UP:
                return bb.maxY;
            case DOWN:
                return bb.minY;
            case SOUTH:
                return bb.maxZ;
            case NORTH:
                return bb.minZ;
        }
        return 0;
    }
    
    public static double getMin(AABB bb, team.creative.creativecore.common.util.math.base.Axis axis) {
        switch (axis) {
            case X:
                return bb.minX;
            case Y:
                return bb.minY;
            case Z:
                return bb.minZ;
            default:
                return 0;
        }
    }
    
    public static double getMax(AABB bb, team.creative.creativecore.common.util.math.base.Axis axis) {
        switch (axis) {
            case X:
                return bb.maxX;
            case Y:
                return bb.maxY;
            case Z:
                return bb.maxZ;
            default:
                return 0;
        }
    }
    
    public static double getMin(AABB bb, Axis axis) {
        switch (axis) {
            case X:
                return bb.minX;
            case Y:
                return bb.minY;
            case Z:
                return bb.minZ;
            default:
                return 0;
        }
    }
    
    public static double getMax(AABB bb, Axis axis) {
        switch (axis) {
            case X:
                return bb.maxX;
            case Y:
                return bb.maxY;
            case Z:
                return bb.maxZ;
            default:
                return 0;
        }
    }
    
    public static Vec3d getCorner(AABB bb, BoxCorner corner) {
        return new Vec3d(getCornerX(bb, corner), getCornerY(bb, corner), getCornerZ(bb, corner));
    }
    
    public static double getCornerValue(AABB bb, BoxCorner corner, team.creative.creativecore.common.util.math.base.Axis axis) {
        return get(bb, corner.getFacing(axis));
    }
    
    public static double getCornerX(AABB bb, BoxCorner corner) {
        return get(bb, corner.x);
    }
    
    public static double getCornerY(AABB bb, BoxCorner corner) {
        return get(bb, corner.y);
    }
    
    public static double getCornerZ(AABB bb, BoxCorner corner) {
        return get(bb, corner.z);
    }
    
    @Override
    protected DoubleList getCoords(Axis axis) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public double min(Direction.Axis axis) {
        return bb.min(axis);
    }
    
    @Override
    public double max(Direction.Axis axis) {
        return bb.max(axis);
    }
    
    @Override
    public AABB bounds() {
        return bb;
    }
    
    @Override
    protected double get(Direction.Axis axis, int p_83258_) {
        return this.getCoords(axis).getDouble(p_83258_);
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public VoxelShape move(double x, double y, double z) {
        return create(bb.move(x, y, z));
    }
    
    @Override
    public VoxelShape optimize() {
        return this;
    }
    
    @Override
    public void forAllEdges(Shapes.DoubleLineConsumer consumer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void forAllBoxes(Shapes.DoubleLineConsumer consumer) {
        consumer.consume(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }
    
    @Override
    public List<AABB> toAabbs() {
        return new SingletonList<>(bb);
    }
    
    @Override
    public double min(Direction.Axis axis, double one, double two) {
        return min(axis);
    }
    
    @Override
    public double max(Direction.Axis axis, double one, double two) {
        return max(axis);
    }
    
    @Override
    protected int findIndex(Direction.Axis axis, double value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Nullable
    public BlockHitResult clip(Vec3 pos, Vec3 look, BlockPos block) {
        return AABB.clip(new SingletonList<>(bb), pos, look, block);
    }
    
    @Override
    public Optional<Vec3> closestPointTo(Vec3 vec) {
        Vec3 avec3 = null;
        double d0 = Mth.clamp(vec.x(), bb.minX, bb.maxX);
        double d1 = Mth.clamp(vec.y(), bb.minY, bb.maxY);
        double d2 = Mth.clamp(vec.z(), bb.minZ, bb.maxZ);
        if (avec3 == null || vec.distanceToSqr(d0, d1, d2) < vec.distanceToSqr(avec3))
            avec3 = new Vec3(d0, d1, d2);
        return Optional.of(avec3);
    }
    
    @Override
    public VoxelShape getFaceShape(Direction p_83264_) {
        return this;
    }
    
    public double collideStepUp(AABB other, AABB otherY, double offset) {
        double newOffset = collide(Axis.Y, otherY, offset);
        if (offset > 0) {
            if (newOffset < offset)
                return newOffset / 2;
        } else {
            if (newOffset > offset)
                return newOffset / 2;
        }
        
        return newOffset;
    }
    
    @Override
    public double collide(Direction.Axis axis, AABB bb, double value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected double collideX(AxisCycle cycle, AABB bb, double distance) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return "AABBVoxelShape[" + bb + "]";
    }
}
