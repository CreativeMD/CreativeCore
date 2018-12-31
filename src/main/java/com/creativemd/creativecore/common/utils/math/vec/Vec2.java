package com.creativemd.creativecore.common.utils.math.vec;

public class Vec2 extends Vec {
	
	public double x;
	public double y;
	
	public Vec2(Vec vec) {
		super(vec);
	}
	
	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public double getValueByDim(int dim) {
		if (dim == 0)
			return x;
		if (dim == 1)
			return y;
		return 0;
	}
	
	@Override
	public void setValueByDim(int dim, double value) {
		if (dim == 0)
			this.x = value;
		if (dim == 1)
			this.y = value;
	}
	
	@Override
	public int getDimensionCount() {
		return 2;
	}
}
