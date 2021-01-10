package com.creativemd.creativecore.common.utils.math.box;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.creativemd.creativecore.common.utils.math.Rotation;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

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
    
    public AlignedBox(AxisAlignedBB box) {
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
    
    public Vector3d getSize() {
        return new Vector3d(maxX - minX, maxY - minY, maxZ - minZ);
    }
    
    @Override
    public String toString() {
        return "cube[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
    
    public AxisAlignedBB getBB() {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    public AxisAlignedBB getBB(BlockPos pos) {
        return new AxisAlignedBB(minX + pos.getX(), minY + pos.getY(), minZ + pos.getZ(), maxX + pos.getX(), maxY + pos.getY(), maxZ + pos.getZ());
    }
    
    public void rotate(Rotation rotation, Vector3f center) {
        Vector3f low = new Vector3f(minX, minY, minZ);
        Vector3f high = new Vector3f(maxX, maxY, maxZ);
        
        low.sub(center);
        high.sub(center);
        
        rotation.getMatrix().transform(low);
        rotation.getMatrix().transform(high);
        
        low.add(center);
        high.add(center);
        
        set(low.x, low.y, low.z, high.x, high.y, high.z);
    }
    
    public void rotate(EnumFacing facing, Vector3f center) {
        Matrix3f matrix = new Matrix3f();
        if (facing.getAxis() == Axis.X)
            facing = facing.getOpposite();
        matrix.rotY((float) Math.toRadians(facing.getHorizontalAngle()));
        rotate(matrix, center);
    }
    
    public void rotate(Matrix3f matrix, Vector3f center) {
        Vector3f low = new Vector3f(minX, minY, minZ);
        Vector3f high = new Vector3f(maxX, maxY, maxZ);
        
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
    
    public float getValueOfFacing(EnumFacing facing) {
        switch (facing) {
        case EAST:
            return maxX;
        case WEST:
            return minX;
        case UP:
            return maxY;
        case DOWN:
            return minY;
        case SOUTH:
            return maxZ;
        case NORTH:
            return minZ;
        
        }
        return 0;
    }
    
    public float getSize(Axis axis) {
        switch (axis) {
        case X:
            return maxX - minX;
        case Y:
            return maxY - minY;
        case Z:
            return maxZ - minZ;
        }
        return 0;
    }
    
    public void setMin(Axis axis, float value) {
        switch (axis) {
        case X:
            minX = value;
            break;
        case Y:
            minY = value;
            break;
        case Z:
            minZ = value;
            break;
        }
    }
    
    public float getMin(Axis axis) {
        switch (axis) {
        case X:
            return minX;
        case Y:
            return minY;
        case Z:
            return minZ;
        }
        return 0;
    }
    
    public void setMax(Axis axis, float value) {
        switch (axis) {
        case X:
            maxX = value;
            break;
        case Y:
            maxY = value;
            break;
        case Z:
            maxZ = value;
            break;
        }
    }
    
    public float getMax(Axis axis) {
        switch (axis) {
        case X:
            return maxX;
        case Y:
            return maxY;
        case Z:
            return maxZ;
        }
        return 0;
    }
    
    public void shrink(Axis axis, float value) {
        value /= 2;
        setMin(axis, getMin(axis) + value);
        setMax(axis, getMax(axis) - value);
    }
    
}
