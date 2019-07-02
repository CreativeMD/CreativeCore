package com.creativemd.creativecore.common.utils.math;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.Vec3i;

public enum Rotation {
	
	X_CLOCKWISE(Axis.X, new RotationMatrix(1, 0, 0, 0, 0, -1, 0, 1, 0), true) {
		@Override
		public Rotation getOpposite() {
			return X_COUNTER_CLOCKWISE;
		}
	},
	
	X_COUNTER_CLOCKWISE(Axis.X, new RotationMatrix(1, 0, 0, 0, 0, 1, 0, -1, 0), false) {
		@Override
		public Rotation getOpposite() {
			return X_CLOCKWISE;
		}
	},
	
	Y_CLOCKWISE(Axis.Y, new RotationMatrix(0, 0, 1, 0, 1, 0, -1, 0, 0), true) {
		@Override
		public Rotation getOpposite() {
			return Rotation.Y_COUNTER_CLOCKWISE;
		}
	},
	Y_COUNTER_CLOCKWISE(Axis.Y, new RotationMatrix(0, 0, -1, 0, 1, 0, 1, 0, 0), false) {
		@Override
		public Rotation getOpposite() {
			return Rotation.Y_CLOCKWISE;
		}
	},
	
	Z_CLOCKWISE(Axis.Z, new RotationMatrix(0, -1, 0, 1, 0, 0, 0, 0, 1), true) {
		@Override
		public Rotation getOpposite() {
			return Rotation.Z_COUNTER_CLOCKWISE;
		}
	},
	Z_COUNTER_CLOCKWISE(Axis.Z, new RotationMatrix(0, 1, 0, -1, 0, 0, 0, 0, 1), false) {
		@Override
		public Rotation getOpposite() {
			return Z_CLOCKWISE;
		}
	};
	
	public static Rotation getRotation(Axis axis, boolean clockwise) {
		switch (axis) {
		case X:
			return clockwise ? X_CLOCKWISE : X_COUNTER_CLOCKWISE;
		case Y:
			return clockwise ? Y_CLOCKWISE : Y_COUNTER_CLOCKWISE;
		case Z:
			return clockwise ? Z_CLOCKWISE : Z_COUNTER_CLOCKWISE;
		}
		return null;
	}
	
	public static Rotation getRotation(Vector3d vec) {
		if (vec.x > 0)
			return Rotation.X_CLOCKWISE;
		if (vec.x < 0)
			return Rotation.X_COUNTER_CLOCKWISE;
		if (vec.y > 0)
			return Rotation.Y_CLOCKWISE;
		if (vec.y < 0)
			return Rotation.Y_COUNTER_CLOCKWISE;
		if (vec.z > 0)
			return Rotation.Z_CLOCKWISE;
		if (vec.z < 0)
			return Rotation.Z_COUNTER_CLOCKWISE;
		return null;
	}
	
	public final Axis axis;
	public final int direction;
	public final boolean clockwise;
	private final Vector3d vec;
	private final RotationMatrix rotationMatrix;
	
	private Rotation(Axis axis, RotationMatrix matrix, boolean clockwise) {
		this.rotationMatrix = matrix;
		this.axis = axis;
		this.clockwise = clockwise;
		this.direction = clockwise ? 1 : -1;
		this.vec = new Vector3d();
		RotationUtils.setValue(vec, direction, axis);
	}
	
	public boolean getRotatedComponentPositive(Axis axis) {
		switch (axis) {
		case X:
			if (rotationMatrix.m00 != 0)
				return rotationMatrix.m00 > 0;
			else if (rotationMatrix.m10 != 0)
				return rotationMatrix.m10 > 0;
			else
				return rotationMatrix.m20 > 0;
		case Y:
			if (rotationMatrix.m01 != 0)
				return rotationMatrix.m01 > 0;
			else if (rotationMatrix.m11 != 0)
				return rotationMatrix.m11 > 0;
			else
				return rotationMatrix.m21 > 0;
		case Z:
			if (rotationMatrix.m02 != 0)
				return rotationMatrix.m02 > 0;
			else if (rotationMatrix.m12 != 0)
				return rotationMatrix.m12 > 0;
			else
				return rotationMatrix.m22 > 0;
		}
		return true;
	}
	
	public Axis getRotatedComponent(Axis axis) {
		switch (axis) {
		case X:
			if (rotationMatrix.m00 != 0)
				return Axis.X;
			else if (rotationMatrix.m10 != 0)
				return Axis.Y;
			else
				return Axis.Z;
		case Y:
			if (rotationMatrix.m01 != 0)
				return Axis.X;
			else if (rotationMatrix.m11 != 0)
				return Axis.Y;
			else
				return Axis.Z;
		case Z:
			if (rotationMatrix.m02 != 0)
				return Axis.X;
			else if (rotationMatrix.m12 != 0)
				return Axis.Y;
			else
				return Axis.Z;
		}
		return axis;
	}
	
	public boolean negativeX() {
		return rotationMatrix.m00 != 0 ? rotationMatrix.m00 < 0 : (rotationMatrix.m01 != 0 ? rotationMatrix.m01 < 0 : rotationMatrix.m02 < 0);
	}
	
	public <T> T getX(T x, T y, T z) {
		return rotationMatrix.m00 != 0 ? x : (rotationMatrix.m01 != 0 ? y : z);
	}
	
	public boolean negativeY() {
		return rotationMatrix.m10 != 0 ? rotationMatrix.m10 < 0 : (rotationMatrix.m11 != 0 ? rotationMatrix.m11 < 0 : rotationMatrix.m12 < 0);
	}
	
	public <T> T getY(T x, T y, T z) {
		return rotationMatrix.m10 != 0 ? x : (rotationMatrix.m11 != 0 ? y : z);
	}
	
	public boolean negativeZ() {
		return rotationMatrix.m20 != 0 ? rotationMatrix.m20 < 0 : (rotationMatrix.m21 != 0 ? rotationMatrix.m21 < 0 : rotationMatrix.m22 < 0);
	}
	
	public <T> T getZ(T x, T y, T z) {
		return rotationMatrix.m20 != 0 ? x : (rotationMatrix.m21 != 0 ? y : z);
	}
	
	public Vector3d getVec() {
		return new Vector3d(vec);
	}
	
	public RotationMatrix getMatrix() {
		return this.rotationMatrix;
	}
	
	public abstract Rotation getOpposite();
	
	public static class RotationMatrix {
		
		public int m00;
		public int m01;
		public int m02;
		public int m10;
		public int m11;
		public int m12;
		public int m20;
		public int m21;
		public int m22;
		
		public RotationMatrix(int m00, int m01, int m02, int m10, int m11, int m12, int m20, int m21, int m22) {
			this.m00 = m00;
			this.m01 = m01;
			this.m02 = m02;
			
			this.m10 = m10;
			this.m11 = m11;
			this.m12 = m12;
			
			this.m20 = m20;
			this.m21 = m21;
			this.m22 = m22;
		}
		
		public RotationMatrix(RotationMatrix m1, RotationMatrix m2) {
			this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
			this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
			this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
			
			this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
			this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
			this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
			
			this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
			this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
			this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
		}
		
		public int getX(int[] vec) {
			return getX(vec[0], vec[1], vec[2]);
		}
		
		public int getX(Vec3i vec) {
			return getX(vec.getX(), vec.getY(), vec.getZ());
		}
		
		public int getX(int x, int y, int z) {
			return x * m00 + y * m01 + z * m02;
		}
		
		public long getX(long x, long y, long z) {
			return x * m00 + y * m01 + z * m02;
		}
		
		public int getY(int[] vec) {
			return getY(vec[0], vec[1], vec[2]);
		}
		
		public int getY(Vec3i vec) {
			return getY(vec.getX(), vec.getY(), vec.getZ());
		}
		
		public int getY(int x, int y, int z) {
			return x * m10 + y * m11 + z * m12;
		}
		
		public long getY(long x, long y, long z) {
			return x * m10 + y * m11 + z * m12;
		}
		
		public int getZ(int[] vec) {
			return getZ(vec[0], vec[1], vec[2]);
		}
		
		public int getZ(Vec3i vec) {
			return getZ(vec.getX(), vec.getY(), vec.getZ());
		}
		
		public int getZ(int x, int y, int z) {
			return x * m20 + y * m21 + z * m22;
		}
		
		public long getZ(long x, long y, long z) {
			return x * m20 + y * m21 + z * m22;
		}
		
		public Vec3i transform(Vec3i vec) {
			int x = vec.getX() * m00 + vec.getY() * m01 + vec.getZ() * m02;
			int y = vec.getX() * m10 + vec.getY() * m11 + vec.getZ() * m12;
			int z = vec.getX() * m20 + vec.getY() * m21 + vec.getZ() * m22;
			return new Vec3i(x, y, z);
		}
		
		public void transform(Tuple3f triple) {
			float x = triple.x * m00 + triple.y * m01 + triple.z * m02;
			float y = triple.x * m10 + triple.y * m11 + triple.z * m12;
			float z = triple.x * m20 + triple.y * m21 + triple.z * m22;
			triple.set(x, y, z);
		}
		
		public void transform(Tuple3d triple) {
			double x = triple.x * m00 + triple.y * m01 + triple.z * m02;
			double y = triple.x * m10 + triple.y * m11 + triple.z * m12;
			double z = triple.x * m20 + triple.y * m21 + triple.z * m22;
			triple.set(x, y, z);
		}
		
		@Override
		public boolean equals(Object object) {
			if (object instanceof RotationMatrix) {
				RotationMatrix m1 = (RotationMatrix) object;
				return (this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
		}
		
	}
	
}
