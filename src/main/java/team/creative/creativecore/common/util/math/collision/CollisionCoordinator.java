package team.creative.creativecore.common.util.math.collision;

import java.util.HashMap;

import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.box.ABB;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.matrix.Matrix3;
import team.creative.creativecore.common.util.math.matrix.Matrix4;
import team.creative.creativecore.common.util.math.utils.BooleanUtils;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class CollisionCoordinator {
    
    private final HashMap<Double, Matrix4> table = new HashMap<>();
    private final HashMap<Double, Matrix4> invertedTable = new HashMap<>();
    
    private final double rotXRadians;
    private final double rotYRadians;
    private final double rotZRadians;
    
    public final double rotX;
    public final double rotY;
    public final double rotZ;
    public final double offX;
    public final double offY;
    public final double offZ;
    
    public Matrix3 rotationX;
    public Matrix3 rotationY;
    public Matrix3 rotationZ;
    public Matrix3 rotationXInv;
    public Matrix3 rotationYInv;
    public Matrix3 rotationZInv;
    public Vec3d translation;
    
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
    
    private final Vec3d rotationCenter;
    private final IVecOrigin moved;
    private final IVecOrigin original;
    
    private final double originalOffX;
    private final double originalOffY;
    private final double originalOffZ;
    private final double originalRotX;
    private final double originalRotY;
    private final double originalRotZ;
    
    public CollisionCoordinator(double offX, double offY, double offZ, double rotX, double rotY, double rotZ, IVecOrigin origin) {
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
        this.hasOneRotation = BooleanUtils.explicitOneTrue(hasRotX, hasRotY, hasRotZ);
        this.hasRotation = hasRotX || hasRotY || hasRotZ;
        this.isSimple = isSimple();
        
        this.original = origin;
        this.rotationCenter = new Vec3d(origin.center());
        if (origin.getParent() != null)
            origin.getParent().transformPointToWorld(rotationCenter);
        
        this.originalOffX = origin.offX();
        this.originalOffY = origin.offY();
        this.originalOffZ = origin.offZ();
        this.originalRotX = origin.rotX();
        this.originalRotY = origin.rotY();
        this.originalRotZ = origin.rotZ();
        
        this.moved = original.copy();
        this.moved.off(originalOffX + offX, originalOffY + offY, originalOffZ + offZ);
        this.moved.rot(originalRotX + rotX, originalRotY + rotY, originalRotZ + rotZ);
        
        this.translation = hasTranslation ? new Vec3d(offX, offY, offZ) : null;
        
    }
    
    public boolean hasOnlyTranslation() {
        return hasTranslation && !hasRotX && !hasRotY && !hasRotZ;
    }
    
    public double getRotationDegree(Axis axis) {
        return switch (axis) {
            case X -> rotX;
            case Y -> rotY;
            case Z -> rotZ;
            default -> 0;
        };
    }
    
    public Matrix3 getRotationMatrix(Axis axis) {
        return switch (axis) {
            case X -> {
                if (rotationX == null && hasRotX)
                    rotationX = MatrixUtils.createRotationMatrixXRadians(rotXRadians);
                yield rotationX;
            }
            case Y -> {
                if (rotationY == null && hasRotY)
                    rotationY = MatrixUtils.createRotationMatrixYRadians(rotYRadians);
                yield rotationY;
            }
            case Z -> {
                if (rotationZ == null && hasRotZ)
                    rotationZ = MatrixUtils.createRotationMatrixZRadians(rotZRadians);
                yield rotationZ;
            }
            default -> null;
        };
    }
    
    public Matrix3 getRotationMatrixInv(Axis axis) {
        return switch (axis) {
            case X -> {
                if (rotationXInv == null && hasRotX)
                    rotationXInv = MatrixUtils.createRotationMatrixXRadians(-rotXRadians);
                yield rotationXInv;
            }
            case Y -> {
                if (rotationYInv == null && hasRotY)
                    rotationYInv = MatrixUtils.createRotationMatrixYRadians(-rotYRadians);
                yield rotationYInv;
            }
            case Z -> {
                if (rotationZInv == null && hasRotZ)
                    rotationZInv = MatrixUtils.createRotationMatrixZRadians(-rotZRadians);
                yield rotationZInv;
            }
            default -> null;
        };
    }
    
    public IVecOrigin original() {
        return original;
    }
    
    public IVecOrigin moved() {
        return moved;
    }
    
    public ABB computeSurroundingBox(ABB box) {
        return box.createRotatedSurrounding(this);
    }
    
    public ABB computeInverseSurroundingBoxInternal(ABB box) {
        return box.createRotatedSurroundingInverseInternal(this);
    }
    
    private boolean isSimple() {
        if (BooleanUtils.explicitOneTrue(hasRotX, hasRotY, hasRotZ, hasTranslation)) {
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
    
    public Matrix4 getInverted(Double delta) {
        Matrix4 matrix = invertedTable.get(delta);
        if (matrix != null)
            return matrix;
        
        matrix = table.get(delta);
        if (matrix != null) {
            matrix = new Matrix4(matrix);
            matrix.invert();
        } else
            matrix = MatrixUtils.createRotationMatrixAndTranslationRadians(-offX * delta, -offY * delta, -offZ * delta, -rotXRadians * delta, -rotYRadians * delta,
                -rotZRadians * delta);
        
        invertedTable.put(delta, matrix);
        return matrix;
    }
    
    public Matrix4 get(Double delta) {
        Matrix4 matrix = table.get(delta);
        if (matrix != null)
            return matrix;
        
        matrix = invertedTable.get(delta);
        if (matrix != null) {
            matrix = new Matrix4(matrix);
            matrix.invert();
        } else
            matrix = MatrixUtils.createRotationMatrixAndTranslationRadians(offX * delta, offY * delta, offZ * delta, rotXRadians * delta, rotYRadians * delta, rotZRadians * delta);
        
        table.put(delta, matrix);
        return matrix;
    }
    
    public void transform(Vec3d vec, Double delta) {
        if (delta <= 0)
            return;
        
        vec.sub(rotationCenter);
        Matrix4 matrix = get(delta);
        double x, y;
        x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
        y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
        vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
        vec.x = x;
        vec.y = y;
        vec.add(rotationCenter);
    }
    
    public void transformInverted(Vec3d vec, Double delta) {
        if (delta <= 0)
            return;
        
        vec.sub(rotationCenter);
        Matrix4 matrix = getInverted(delta);
        double x, y;
        x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
        y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
        vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
        vec.x = x;
        vec.y = y;
        vec.add(rotationCenter);
    }
    
    public void transform(Matrix4 matrix, Vec3d vec) {
        vec.sub(rotationCenter);
        double x, y;
        x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
        y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
        vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
        vec.x = x;
        vec.y = y;
        vec.add(rotationCenter);
    }
    
    public void finish() {
        this.original.off(originalOffX + offX, originalOffY + offY, originalOffZ + offZ);
        this.original.rot(originalRotX + rotX, originalRotY + rotY, originalRotZ + rotZ);
    }
    
}
