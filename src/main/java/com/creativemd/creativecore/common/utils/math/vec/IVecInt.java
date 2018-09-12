package com.creativemd.creativecore.common.utils.math.vec;

import net.minecraft.util.EnumFacing.Axis;

public interface IVecInt {

	public int getX();

	public int getY();

	public int getZ();

	public void setX(int value);

	public void setY(int value);

	public void setZ(int value);

	public int get(Axis axis);

	public void set(Axis axis, int value);

}
