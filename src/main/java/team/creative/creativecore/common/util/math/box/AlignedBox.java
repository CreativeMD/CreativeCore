package team.creative.creativecore.common.util.math.box;

import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.matrix.Matrix3;
import team.creative.creativecore.common.util.math.transformation.Rotation;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public class AlignedBox {
    
    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;
    
    public AlignedBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }
    
    public AlignedBox(AABB box) {
        this((float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
    }
    
    public AlignedBox() {
        this(0, 0, 0, 1, 1, 1);
    }
    
    public AlignedBox(AlignedBox cube) {
        this(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ);
    }
    
    public void add(float x, float y, float z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
    }
    
    public void sub(float x, float y, float z) {
        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;
        this.maxX -= x;
        this.maxY -= y;
        this.maxZ -= z;
    }
    
    public void add(Vector3d vec) {
        add((float) vec.x, (float) vec.y, (float) vec.z);
    }
    
    public void sub(Vector3d vec) {
        sub((float) vec.x, (float) vec.y, (float) vec.z);
    }
    
    public void add(Vec3i vec) {
        add(vec.getX(), vec.getY(), vec.getZ());
    }
    
    public void sub(Vec3i vec) {
        sub(vec.getX(), vec.getY(), vec.getZ());
    }
    
    public void scale(float scale) {
        this.minX *= scale;
        this.minY *= scale;
        this.minZ *= scale;
        this.maxX *= scale;
        this.maxY *= scale;
        this.maxZ *= scale;
    }
    
    public float getSize(Axis axis) {
        return switch (axis) {
            case X -> maxX - minX;
            case Y -> maxY - minY;
            case Z -> maxZ - minZ;
        };
    }
    
    public Vec3d getSize() {
        return new Vec3d(maxX - minX, maxY - minY, maxZ - minZ);
    }
    
    public Vec3d getCenter() {
        return new Vec3d((maxX + minX) * 0.5, (maxY + minY) * 0.5, (maxZ + minZ) * 0.5);
    }
    
    @Override
    public String toString() {
        return "cube[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
    
    public Vec3f getCorner(BoxCorner corner) {
        return new Vec3f(get(corner.x), get(corner.y), get(corner.z));
    }
    
    public AABB getBB() {
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    public AABB getBB(BlockPos pos) {
        return new AABB(minX + pos.getX(), minY + pos.getY(), minZ + pos.getZ(), maxX + pos.getX(), maxY + pos.getY(), maxZ + pos.getZ());
    }
    
    public VoxelShape voxelShape() {
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    public VoxelShape voxelShape(BlockPos pos) {
        return Shapes.box(minX + pos.getX(), minY + pos.getY(), minZ + pos.getZ(), maxX + pos.getX(), maxY + pos.getY(), maxZ + pos.getZ());
    }
    
    public void rotate(Rotation rotation, Vec3f center) {
        Vec3f low = new Vec3f(minX, minY, minZ);
        Vec3f high = new Vec3f(maxX, maxY, maxZ);
        
        low.sub(center);
        high.sub(center);
        
        rotation.getMatrix().transform(low);
        rotation.getMatrix().transform(high);
        
        low.add(center);
        high.add(center);
        
        set(low.x, low.y, low.z, high.x, high.y, high.z);
    }
    
    public void rotate(Matrix3 matrix, Vec3f center) {
        Vec3f low = new Vec3f(minX, minY, minZ);
        Vec3f high = new Vec3f(maxX, maxY, maxZ);
        
        low.sub(center);
        high.sub(center);
        
        matrix.transform(low);
        matrix.transform(high);
        
        low.add(center);
        high.add(center);
        
        set(low.x, low.y, low.z, high.x, high.y, high.z);
    }
    
    public void set(float x, float y, float z, float x2, float y2, float z2) {
        this.minX = Math.min(x, x2);
        this.minY = Math.min(y, y2);
        this.minZ = Math.min(z, z2);
        this.maxX = Math.max(x, x2);
        this.maxY = Math.max(y, y2);
        this.maxZ = Math.max(z, z2);
    }
    
    public BlockPos getOffset() {
        return new BlockPos(minX, minY, minZ);
    }
    
    public float get(Facing facing) {
        return switch (facing) {
            case EAST -> maxX;
            case WEST -> minX;
            case UP -> maxY;
            case DOWN -> minY;
            case SOUTH -> maxZ;
            case NORTH -> minZ;
        };
    }
    
    public void set(Facing facing, float value) {
        switch (facing) {
            case EAST -> maxX = value;
            case WEST -> minX = value;
            case UP -> maxY = value;
            case DOWN -> minY = value;
            case SOUTH -> maxZ = value;
            case NORTH -> minZ = value;
        };
    }
    
    public void setMin(Axis axis, float value) {
        switch (axis) {
            case X -> minX = value;
            case Y -> minY = value;
            case Z -> minZ = value;
        }
    }
    
    public float getMin(Axis axis) {
        return switch (axis) {
            case X -> minX;
            case Y -> minY;
            case Z -> minZ;
        };
    }
    
    public void setMax(Axis axis, float value) {
        switch (axis) {
            case X -> maxX = value;
            case Y -> maxY = value;
            case Z -> maxZ = value;
        }
    }
    
    public float getMax(Axis axis) {
        return switch (axis) {
            case X -> maxX;
            case Y -> maxY;
            case Z -> maxZ;
        };
    }
    
    public void grow(Axis axis, float value) {
        value /= 2;
        setMin(axis, getMin(axis) - value);
        setMax(axis, getMax(axis) + value);
    }
    
    public void shrink(Axis axis, float value) {
        value /= 2;
        setMin(axis, getMin(axis) + value);
        setMax(axis, getMax(axis) - value);
    }
    
}
