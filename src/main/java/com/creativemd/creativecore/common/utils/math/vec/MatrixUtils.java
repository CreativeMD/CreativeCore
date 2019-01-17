package com.creativemd.creativecore.common.utils.math.vec;

import java.util.HashMap;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.BooleanUtils;

public class MatrixUtils {
	
	public static Matrix3d createIdentityMatrix() {
		Matrix3d matrix = new Matrix3d();
		matrix.setIdentity();
		return matrix;
	}
	
	public static Matrix3d createRotationMatrix(double rotX, double rotY, double rotZ) {
		Matrix3d matrix = createRotationMatrixX(rotX);
		matrix.mul(createRotationMatrixY(rotY));
		matrix.mul(createRotationMatrixZ(rotZ));
		return matrix;
	}
	
	public static void mul(Matrix4d matrix, Matrix3d matrix2) {
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
	
	private static Matrix4d createRotationMatrixAndTranslationRadians(double x, double y, double z, double rotX, double rotY, double rotZ) {
		Matrix4d matrix = new Matrix4d();
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
	
	public static Matrix4d createRotationMatrixAndTranslation(double x, double y, double z, double rotX, double rotY, double rotZ) {
		Matrix4d matrix = new Matrix4d();
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
	
	public static Matrix3d createRotationMatrixX(double angle) {
		Matrix3d matrix = new Matrix3d();
		matrix.rotX(Math.toRadians(angle));
		return matrix;
	}
	
	private static Matrix3d createRotationMatrixXRadians(double radians) {
		Matrix3d matrix = new Matrix3d();
		matrix.rotX(radians);
		return matrix;
	}
	
	public static Matrix3d createRotationMatrixY(double angle) {
		Matrix3d matrix = new Matrix3d();
		matrix.rotY(Math.toRadians(angle));
		return matrix;
	}
	
	private static Matrix3d createRotationMatrixYRadians(double radians) {
		Matrix3d matrix = new Matrix3d();
		matrix.rotY(radians);
		return matrix;
	}
	
	public static Matrix3d createRotationMatrixZ(double angle) {
		Matrix3d matrix = new Matrix3d();
		matrix.rotZ(Math.toRadians(angle));
		return matrix;
	}
	
	private static Matrix3d createRotationMatrixZRadians(double radians) {
		Matrix3d matrix = new Matrix3d();
		matrix.rotZ(radians);
		return matrix;
	}
	
	public static class MatrixLookupTable {
		
		private HashMap<Double, Matrix4d> table = new HashMap<>();
		private HashMap<Double, Matrix4d> invertedTable = new HashMap<>();
		
		private final double rotXRadians;
		private final double rotYRadians;
		private final double rotZRadians;
		
		public final double rotX;
		public final double rotY;
		public final double rotZ;
		public final double x;
		public final double y;
		public final double z;
		
		public final boolean hasRotX;
		public final boolean hasRotY;
		public final boolean hasRotZ;
		public final boolean hasX;
		public final boolean hasY;
		public final boolean hasZ;
		public final boolean hasTranslation;
		public final boolean hasOneRotation;
		public final boolean isSimple;
		
		public final Vector3d rotationCenter;
		public final IVecOrigin origin;
		
		public MatrixLookupTable(double x, double y, double z, double rotX, double rotY, double rotZ, Vector3d rotationCenter, IVecOrigin origin) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.rotX = rotX;
			this.rotY = rotY;
			this.rotZ = rotZ;
			this.rotXRadians = Math.toRadians(rotX);
			this.rotYRadians = Math.toRadians(rotY);
			this.rotZRadians = Math.toRadians(rotZ);
			
			this.hasRotX = rotX != 0;
			this.hasRotY = rotY != 0;
			this.hasRotZ = rotZ != 0;
			this.hasX = x != 0;
			this.hasY = y != 0;
			this.hasZ = z != 0;
			this.hasTranslation = hasX || hasY || hasZ;
			this.hasOneRotation = BooleanUtils.oneTrue(hasRotX, hasRotY, hasRotZ);
			this.isSimple = isSimple();
			
			this.rotationCenter = rotationCenter;
			this.origin = origin;
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
				matrix = createRotationMatrixAndTranslationRadians(-x * delta, -y * delta, -z * delta, -rotXRadians * delta, -rotYRadians * delta, -rotZRadians * delta);
			
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
				matrix = createRotationMatrixAndTranslationRadians(x * delta, y * delta, z * delta, rotXRadians * delta, rotYRadians * delta, rotZRadians * delta);
			
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
		
		public static void transform(Matrix4d matrix, Vector3d rotationCenter, Vector3d vec) {
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
	
}
