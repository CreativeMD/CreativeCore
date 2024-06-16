package team.creative.creativecore.common.util.math.box;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleComparators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.creative.creativecore.common.util.type.list.SingletonList;
import team.creative.creativecore.common.util.unsafe.CreativeHackery;
import team.creative.creativecore.mixin.VoxelShapeAccessor;

public class BoxesVoxelShape extends SliceShape {
    
    public static BoxesVoxelShape create(List<ABB> boxes) {
        BoxesVoxelShape shape = CreativeHackery.allocateInstance(BoxesVoxelShape.class);
        shape.boxes = boxes;
        ((VoxelShapeAccessor) shape).setShape(DISCRETE_SHAPE);
        return shape;
    }
    
    public static BoxesVoxelShape create(ABB box) {
        return create(new SingletonList<ABB>(box));
    }
    
    public static final DiscreteVoxelShape DISCRETE_SHAPE = new DiscreteVoxelShape(1, 1, 1) {
        
        @Override
        public boolean isFull(int x, int y, int z) {
            return true;
        }
        
        @Override
        public void fill(int x, int y, int z) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int firstFull(Axis axis) {
            return 0;
        }
        
        @Override
        public int lastFull(Axis axis) {
            return 1;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
    };
    
    public List<ABB> boxes;
    
    protected BoxesVoxelShape() {
        super(null, null, 0);
    }
    
    @Override
    public VoxelShape move(double x, double y, double z) {
        List<ABB> boxes = new ArrayList<>();
        for (ABB bb : this.boxes)
            boxes.add(bb.moveCopy(x, y, z));
        
        return create(boxes);
    }
    
    @Override
    public DoubleList getCoords(Axis axis) {
        DoubleArrayList list = new DoubleArrayList(boxes.size() * 2);
        for (ABB bb : boxes) {
            list.add(bb.min(axis));
            list.add(bb.max(axis));
        }
        list.sort(DoubleComparators.NATURAL_COMPARATOR);
        return list;
    }
    
    @Override
    protected double get(Direction.Axis axis, int index) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public double min(Direction.Axis axis) {
        double min = Double.POSITIVE_INFINITY;
        for (ABB bb : boxes)
            min = Math.min(min, bb.min(axis));
        return min;
    }
    
    @Override
    public double max(Direction.Axis axis) {
        double max = Double.NEGATIVE_INFINITY;
        for (ABB bb : boxes)
            max = Math.max(max, bb.max(axis));
        return max;
    }
    
    @Override
    public void forAllEdges(Shapes.DoubleLineConsumer consumer) {
        for (ABB bb : boxes)
            for (BoxFace face : BoxFace.values()) {
                forAllEdgesCorner(bb, face.corners[0], face.corners[1], consumer);
                forAllEdgesCorner(bb, face.corners[1], face.corners[2], consumer);
                forAllEdgesCorner(bb, face.corners[2], face.corners[3], consumer);
                forAllEdgesCorner(bb, face.corners[3], face.corners[0], consumer);
            }
    }
    
    private void forAllEdgesCorner(ABB bb, BoxCorner corner1, BoxCorner corner2, Shapes.DoubleLineConsumer consumer) {
        consumer.consume(bb.cornerX(corner1), bb.cornerY(corner1), bb.cornerZ(corner1), bb.cornerX(corner2), bb.cornerY(corner2), bb.cornerZ(corner2));
    }
    
    @Override
    public void forAllBoxes(Shapes.DoubleLineConsumer consumer) {
        for (ABB bb : boxes)
            consumer.consume(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }
    
    @Override
    public List<AABB> toAabbs() {
        List<AABB> bbs = new ArrayList<>(boxes.size());
        for (ABB bb : boxes)
            bbs.add(bb.toVanilla());
        return bbs;
    }
    
    @Override
    public double min(Direction.Axis axis, double one, double two) {
        // Only used with Axis.Y, one as X, two as Z
        team.creative.creativecore.common.util.math.base.Axis axisOne = team.creative.creativecore.common.util.math.base.Axis.X;
        team.creative.creativecore.common.util.math.base.Axis axisTwo = team.creative.creativecore.common.util.math.base.Axis.Z;
        double min = Double.POSITIVE_INFINITY;
        for (ABB bb : boxes)
            if (bb.intersectsWithAxis(axisOne, axisTwo, one, two))
                min = Math.min(min, bb.min(axis));
        return min;
    }
    
    @Override
    public double max(Direction.Axis axis, double one, double two) {
        // Only used with Axis.Y, one as X, two as Z
        team.creative.creativecore.common.util.math.base.Axis axisOne = team.creative.creativecore.common.util.math.base.Axis.X;
        team.creative.creativecore.common.util.math.base.Axis axisTwo = team.creative.creativecore.common.util.math.base.Axis.Z;
        double max = Double.NEGATIVE_INFINITY;
        for (ABB bb : boxes)
            if (bb.intersectsWithAxis(axisOne, axisTwo, one, two))
                max = Math.max(max, bb.max(axis));
        return max;
    }
    
    @Override
    protected int findIndex(Direction.Axis axis, double value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Nullable
    public BlockHitResult clip(Vec3 pos, Vec3 look, BlockPos block) {
        return ABB.clip(boxes, pos, look, block);
    }
    
    @Override
    public Optional<Vec3> closestPointTo(Vec3 vec) {
        Vec3 avec3 = null;
        for (ABB bb : boxes) {
            double d0 = Mth.clamp(vec.x(), bb.minX, bb.maxX);
            double d1 = Mth.clamp(vec.y(), bb.minY, bb.maxY);
            double d2 = Mth.clamp(vec.z(), bb.minZ, bb.maxZ);
            if (avec3 == null || vec.distanceToSqr(d0, d1, d2) < vec.distanceToSqr(avec3))
                avec3 = new Vec3(d0, d1, d2);
        }
        
        return Optional.of(avec3);
    }
    
    @Override
    public VoxelShape getFaceShape(Direction direction) {
        return this;
    }
    
    @Override
    public boolean isEmpty() {
        return boxes.isEmpty();
    }
    
    @Override
    public VoxelShape optimize() {
        return this;
    }
    
    public boolean intersectsWith(AABB bb) {
        for (ABB abb : boxes)
            if (abb.intersectsPrecise(bb))
                return true;
        return false;
    }
    
    /** Removes all boxes which are not intersecting with the parameter.
     *
     * @param bb
     * @return whether if there is at least one box intersecting */
    public boolean onlyKeepIntersecting(AABB bb) {
        boxes.removeIf(x -> x.intersectsPrecise(bb));
        return !boxes.isEmpty();
    }
    
    @Override
    public double collide(Direction.Axis axis, AABB other, double distance) {
        if (this.isEmpty())
            return distance;
        
        if (Math.abs(distance) < 1.0E-7D)
            return 0.0D;
        
        team.creative.creativecore.common.util.math.base.Axis ltAxis = team.creative.creativecore.common.util.math.base.Axis.get(axis);
        team.creative.creativecore.common.util.math.base.Axis one = ltAxis.one();
        team.creative.creativecore.common.util.math.base.Axis two = ltAxis.two();
        
        for (ABB bb : boxes)
            if (distance > 0)
                distance = Math.min(distance, bb.calculateAxisOffset(ltAxis, one, two, other, distance));
            else
                distance = Math.max(distance, bb.calculateAxisOffset(ltAxis, one, two, other, distance));
            
        return distance;
    }
    
    @Override
    protected double collideX(AxisCycle cycle, AABB other, double distance) {
        if (this.isEmpty())
            return distance;
        
        if (Math.abs(distance) < 1.0E-7D)
            return 0.0D;
        
        AxisCycle axiscycle = cycle.inverse();
        team.creative.creativecore.common.util.math.base.Axis axis = team.creative.creativecore.common.util.math.base.Axis.get(axiscycle.cycle(Direction.Axis.X));
        team.creative.creativecore.common.util.math.base.Axis one = team.creative.creativecore.common.util.math.base.Axis.get(axiscycle.cycle(Direction.Axis.Y));
        team.creative.creativecore.common.util.math.base.Axis two = team.creative.creativecore.common.util.math.base.Axis.get(axiscycle.cycle(Direction.Axis.Z));
        
        for (ABB bb : boxes)
            if (distance > 0)
                distance = Math.min(distance, bb.calculateAxisOffset(axis, one, two, other, distance));
            else
                distance = Math.max(distance, bb.calculateAxisOffset(axis, one, two, other, distance));
            
        return distance;
    }
    
    @Override
    public AABB bounds() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        for (ABB bb : boxes) {
            minX = Math.min(minX, bb.minX);
            minY = Math.min(minY, bb.minY);
            minZ = Math.min(minZ, bb.minZ);
            maxX = Math.max(maxX, bb.maxX);
            maxY = Math.max(maxY, bb.maxY);
            maxZ = Math.max(maxZ, bb.maxZ);
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    @Override
    public String toString() {
        return "AABBListVoxelShape[" + boxes + "]";
    }
}
