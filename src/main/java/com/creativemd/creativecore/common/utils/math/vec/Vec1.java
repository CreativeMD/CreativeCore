package com.creativemd.creativecore.common.utils.math.vec;

public class Vec1 extends Vec<Vec1> {
	
	public double x;
	
	public Vec1(Vec vec) {
		super(vec);
	}
	
	public Vec1(double x) {
		this.x = x;
	}
	
	@Override
	public double getValueByDim(int dim) {
		if (dim == 0)
			return x;
		return 0;
	}
	
	@Override
	public void setValueByDim(int dim, double value) {
		if (dim == 0)
			this.x = value;
	}
	
	@Override
	public int getDimensionCount() {
		return 1;
	}
}
