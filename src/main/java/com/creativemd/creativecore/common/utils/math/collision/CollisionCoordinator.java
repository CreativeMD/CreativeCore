package com.creativemd.creativecore.common.utils.math.collision;

import java.util.HashMap;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.BooleanUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils;
import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;

import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;

public class CollisionCoordinator {
    
    private HashMap<Double, Matrix4d> table = new HashMap<>();
    private HashMap<Double, Matrix4d> invertedTable = new HashMap<>();
    
    private final double rotXRadians;
    private final double rotYRadians;
    private final double rotZRadians;
    
    public final double rotX;
    public final double rotY;
    public final double rotZ;
    public final double offX;
    public final double offY;
    public final double offZ;
    
    public Matrix3d rotationX;
    public Matrix3d rotationY;
    public Matrix3d rotationZ;
    public Vector3d translation;
    
    public final boolean hasRotX;
    public final boolean hasRotY;
    public final boolean hasRotZ;
    public final boolean hasOffX;
    public final boolean hasOffY;
    public final boolean hasOffZ;
    public final boolean hasTranslation;
    public final boolean hasOneRotation;
    public final boolean hasRotation;
    public final boolean isSimple;
    
    private final Vector3d rotationCenter;
    public final IVecOrigin moving;
    public IVecOrigin origin;
    
    private final double originalOffX;
    private final double originalOffY;
    private final double originalOffZ;
    private final double originalRotX;
    private final double originalRotY;
    private final double originalRotZ;
    
    public CollisionCoordinator(double offX, double offY, double offZ, double rotX, double rotY, double rotZ, IVecOrigin moving, IVecOrigin origin) {
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.rotXRadians = Math.toRadians(rotX);
        this.rotYRadians = Math.toRadians(rotY);
        this.rotZRadians = Math.toRadians(rotZ);
        
        this.hasRotX = rotX != 0;
        this.hasRotY = rotY != 0;
        this.hasRotZ = rotZ != 0;
        this.hasOffX = offX != 0;
        this.hasOffY = offY != 0;
        this.hasOffZ = offZ != 0;
        this.hasTranslation = hasOffX || hasOffY || hasOffZ;
        this.hasOneRotation = BooleanUtils.oneTrue(hasRotX, hasRotY, hasRotZ);
        this.hasRotation = hasRotX || hasRotY || hasRotZ;
        this.isSimple = isSimple();
        
        this.moving = moving;
        this.rotationCenter = new Vector3d(moving.center());
        if (moving.getParent() != null)
            moving.getParent().transformPointToWorld(rotationCenter);
        this.origin = origin;
        
        this.rotationX = hasRotX ? MatrixUtils.createRotationMatrixX(rotX) : null;
        this.rotationY = hasRotY ? MatrixUtils.createRotationMatrixY(rotY) : null;
        this.rotationZ = hasRotZ ? MatrixUtils.createRotationMatrixZ(rotZ) : null;
        this.translation = hasTranslation ? new Vector3d(offX, offY, offZ) : null;
        
        this.originalOffX = moving.offX();
        this.originalOffY = moving.offY();
        this.originalOffZ = moving.offZ();
        this.originalRotX = moving.rotX();
        this.originalRotY = moving.rotY();
        this.originalRotZ = moving.rotZ();
    }
    
    public void reset(IVecOrigin origin) {
        this.origin = origin;
        this.moving.off(originalOffX, originalOffY, originalOffZ);
        this.moving.rot(originalRotX, originalRotY, originalRotZ);
    }
    
    public void move() {
        this.moving.off(originalOffX + offX, originalOffY + offY, originalOffZ + offZ);
        this.moving.rot(originalRotX + rotX, originalRotY + rotY, originalRotZ + rotZ);
    }
    
    public boolean hasOnlyTranslation() {
        return hasTranslation && !hasRotX && !hasRotY && !hasRotZ;
    }
    
    public double getRotationDegree(Axis axis) {
        switch (axis) {
        case X:
            return rotX;
        case Y:
            return rotY;
        case Z:
            return rotZ;
        default:
            return 0;
        }
    }
    
    public Matrix3d getRotationMatrix(Axis axis) {
        switch (axis) {
        case X:
            return rotationX;
        case Y:
            return rotationY;
        case Z:
            return rotationZ;
        default:
            return null;
        }
    }
    
    public AxisAlignedBB computeSurroundingBox(AxisAlignedBB box) {
        return BoxUtils.getRotatedSurrounding(box, this);
    }
    
    private boolean isSimple() {
        if (BooleanUtils.oneTrue(hasRotX, hasRotY, hasRotZ, hasTranslation)) {
            if (hasRotX)
                return Math.abs(rotX) <= 180;
            else if (hasRotY)
                return Math.abs(rotY) <= 180;
            else if (hasRotZ)
                return Math.abs(rotZ) <= 180;
            return true;
        }
        return false;
    }
    
    public int getNumberOfHalfRotations() {
        int halfRotations = 0;
        if (hasRotX)
            halfRotations += (int) Math.ceil(Math.abs(rotX) / 180);
        if (hasRotY)
            halfRotations += (int) Math.ceil(Math.abs(rotY) / 180);
        if (hasRotZ)
            halfRotations += (int) Math.ceil(Math.abs(rotZ) / 180);
        return halfRotations;
    }
    
    public Matrix4d getInverted(Double delta) {
        Matrix4d matrix = invertedTable.get(delta);
        if (matrix != null)
            return matrix;
        
        matrix = table.get(delta);
        if (matrix != null) {
            matrix = new Matrix4d(matrix);
            matrix.invert();
        } else
            matrix = MatrixUtils.createRotationMatrixAndTranslationRadians(-offX * delta, -offY * delta, -offZ * delta, -rotXRadians * delta, -rotYRadians * delta, -rotZRadians * delta);
        
        invertedTable.put(delta, matrix);
        return matrix;
    }
    
    public Matrix4d get(Double delta) {
        Matrix4d matrix = table.get(delta);
        if (matrix != null)
            return matrix;
        
        matrix = invertedTable.get(delta);
        if (matrix != null) {
            matrix = new Matrix4d(matrix);
            matrix.invert();
        } else
            matrix = MatrixUtils.createRotationMatrixAndTranslationRadians(offX * delta, offY * delta, offZ * delta, rotXRadians * delta, rotYRadians * delta, rotZRadians * delta);
        
        table.put(delta, matrix);
        return matrix;
    }
    
    public void transform(Vector3d vec, Double delta) {
        if (delta <= 0)
            return;
        
        vec.sub(rotationCenter);
        Matrix4d matrix = get(delta);
        double x, y;
        x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
        y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
        vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
        vec.x = x;
        vec.y = y;
        vec.add(rotationCenter);
    }
    
    public void transformInverted(Vector3d vec, Double delta) {
        if (delta <= 0)
            return;
        
        vec.sub(rotationCenter);
        Matrix4d matrix = getInverted(delta);
        double x, y;
        x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
        y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
        vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
        vec.x = x;
        vec.y = y;
        vec.add(rotationCenter);
    }
    
    public void transform(Matrix4d matrix, Vector3d vec) {
        vec.sub(rotationCenter);
        double x, y;
        x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
        y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
        vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
        vec.x = x;
        vec.y = y;
        vec.add(rotationCenter);
    }
    
}
