package team.creative.creativecore.common.util.math.collision;

import team.creative.creativecore.common.util.math.matrix.Matrix3;
import team.creative.creativecore.common.util.math.matrix.Matrix4;

public class MatrixUtils {
    
    public static Matrix3 createIdentityMatrix() {
        Matrix3 matrix = new Matrix3();
        matrix.setIdentity();
        return matrix;
    }
    
    public static Matrix3 createRotationMatrix(double rotX, double rotY, double rotZ) {
        Matrix3 matrix = createRotationMatrixX(rotX);
        matrix.mul(createRotationMatrixY(rotY));
        matrix.mul(createRotationMatrixZ(rotZ));
        return matrix;
    }
    
    public static void mul(Matrix4 matrix, Matrix3 matrix2) {
        double m00, m01, m02, m10, m11, m12, m20, m21, m22;
        
        m00 = matrix.m00 * matrix2.m00 + matrix.m01 * matrix2.m10 + matrix.m02 * matrix2.m20;
        m01 = matrix.m00 * matrix2.m01 + matrix.m01 * matrix2.m11 + matrix.m02 * matrix2.m21;
        m02 = matrix.m00 * matrix2.m02 + matrix.m01 * matrix2.m12 + matrix.m02 * matrix2.m22;
        
        m10 = matrix.m10 * matrix2.m00 + matrix.m11 * matrix2.m10 + matrix.m12 * matrix2.m20;
        m11 = matrix.m10 * matrix2.m01 + matrix.m11 * matrix2.m11 + matrix.m12 * matrix2.m21;
        m12 = matrix.m10 * matrix2.m02 + matrix.m11 * matrix2.m12 + matrix.m12 * matrix2.m22;
        
        m20 = matrix.m20 * matrix2.m00 + matrix.m21 * matrix2.m10 + matrix.m22 * matrix2.m20;
        m21 = matrix.m20 * matrix2.m01 + matrix.m21 * matrix2.m11 + matrix.m22 * matrix2.m21;
        m22 = matrix.m20 * matrix2.m02 + matrix.m21 * matrix2.m12 + matrix.m22 * matrix2.m22;
        
        matrix.m00 = m00;
        matrix.m01 = m01;
        matrix.m02 = m02;
        matrix.m10 = m10;
        matrix.m11 = m11;
        matrix.m12 = m12;
        matrix.m20 = m20;
        matrix.m21 = m21;
        matrix.m22 = m22;
    }
    
    public static Matrix4 createRotationMatrixAndTranslationRadians(double x, double y, double z, double rotX, double rotY, double rotZ) {
        Matrix4 matrix = new Matrix4();
        if (rotX != 0)
            matrix.rotX(rotX);
        else
            matrix.setIdentity();
        
        if (rotY != 0)
            mul(matrix, createRotationMatrixYRadians(rotY));
        if (rotZ != 0)
            mul(matrix, createRotationMatrixZRadians(rotZ));
        
        matrix.m03 = x;
        matrix.m13 = y;
        matrix.m23 = z;
        
        return matrix;
    }
    
    public static Matrix4 createRotationMatrixAndTranslation(double x, double y, double z, double rotX, double rotY, double rotZ) {
        Matrix4 matrix = new Matrix4();
        if (rotX != 0)
            matrix.rotX(Math.toRadians(rotX));
        else
            matrix.setIdentity();
        
        if (rotY != 0)
            mul(matrix, createRotationMatrixY(rotY));
        if (rotZ != 0)
            mul(matrix, createRotationMatrixZ(rotZ));
        
        matrix.m03 = x;
        matrix.m13 = y;
        matrix.m23 = z;
        
        return matrix;
    }
    
    public static Matrix3 createRotationMatrixX(double angle) {
        Matrix3 matrix = new Matrix3();
        matrix.rotX(Math.toRadians(angle));
        return matrix;
    }
    
    protected static Matrix3 createRotationMatrixXRadians(double radians) {
        Matrix3 matrix = new Matrix3();
        matrix.rotX(radians);
        return matrix;
    }
    
    public static Matrix3 createRotationMatrixY(double angle) {
        Matrix3 matrix = new Matrix3();
        matrix.rotY(Math.toRadians(angle));
        return matrix;
    }
    
    private static Matrix3 createRotationMatrixYRadians(double radians) {
        Matrix3 matrix = new Matrix3();
        matrix.rotY(radians);
        return matrix;
    }
    
    public static Matrix3 createRotationMatrixZ(double angle) {
        Matrix3 matrix = new Matrix3();
        matrix.rotZ(Math.toRadians(angle));
        return matrix;
    }
    
    private static Matrix3 createRotationMatrixZRadians(double radians) {
        Matrix3 matrix = new Matrix3();
        matrix.rotZ(radians);
        return matrix;
    }
    
}
