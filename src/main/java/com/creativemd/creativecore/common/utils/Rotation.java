package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;

import javax.vecmath.Matrix3d;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public enum Rotation {
	
	X_CLOCKWISE(Axis.X, new RotationMatrix(1, 0, 0,
			0, 0, -1,
			0, 1, 0)) {
				@Override
				public Rotation getOpposite() {
					return X_COUNTER_CLOCKWISE;
				}
			},
	
	X_COUNTER_CLOCKWISE(Axis.X, new RotationMatrix(1, 0, 0,
			0, 0, 1,
			0, -1, 0)) {
				@Override
				public Rotation getOpposite() {
					return X_CLOCKWISE;
				}
			},
	
	Y_CLOCKWISE(Axis.Y, new RotationMatrix(0, 0, 1,
			0, 1, 0,
			-1, 0, 0)) {
				@Override
				public Rotation getOpposite() {
					return Rotation.Y_COUNTER_CLOCKWISE;
				}
			},
	Y_COUNTER_CLOCKWISE(Axis.Y, new RotationMatrix(0, 0, -1,
			0, 1, 0,
			1, 0, 0)) {
				@Override
				public Rotation getOpposite() {
					return Rotation.Y_CLOCKWISE;
				}
			},
	
	Z_CLOCKWISE(Axis.Z, new RotationMatrix(0, -1, 0,
			1, 0, 0,
			0, 0, 1)) {
				@Override
				public Rotation getOpposite() {
					return Rotation.Z_COUNTER_CLOCKWISE;
				}
			},
	Z_COUNTER_CLOCKWISE(Axis.Z, new RotationMatrix(0, 1, 0,
			-1, 0, 0,
			0, 0, 1)) {
				@Override
				public Rotation getOpposite() {
					return Z_CLOCKWISE;
				}
			};
	
	public static Rotation getRotation(Axis axis, boolean clockwise)
	{
		switch(axis)
		{
		case X:
			return clockwise ? X_CLOCKWISE : X_COUNTER_CLOCKWISE;
		case Y:
			return clockwise ? Y_CLOCKWISE : Y_COUNTER_CLOCKWISE;
		case Z:
			return clockwise ? Z_CLOCKWISE : Z_COUNTER_CLOCKWISE;
		}
		return null;
	}
	
	public final Axis axis;
	private RotationMatrix rotationMatrix;
	
	private Rotation(Axis axis, RotationMatrix matrix) {
		this.rotationMatrix = matrix;
		this.axis = axis;
	}
	
	public RotationMatrix getMatrix()
	{
		return this.rotationMatrix;
	}

	public abstract Rotation getOpposite();
	
}


