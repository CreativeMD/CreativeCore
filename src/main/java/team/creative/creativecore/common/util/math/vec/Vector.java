package team.creative.creativecore.common.util.math.vec;

import net.minecraft.util.Direction.Axis;

public abstract class Vector<T extends Vector> {
	
	public Vector() {
		
	}
	
	public Vector(T vec) {
		set(vec);
	}
	
	public abstract void set(T vec);
	
	public double get(Axis axis) {
		return get(axis.ordinal());
	}
	
	public void set(Axis axis, double value) {
		set(axis.ordinal(), value);
	}
	
	public abstract double get(int dim);
	
	public abstract void set(int dim, double value);
	
	public abstract int dimensions();
	
	public abstract T copy();
	
	public abstract void add(T vec);
	
	public abstract void sub(T vec);
	
	public abstract void scale(double scale);
	
	public void invert() {
		scale(-1);
	}
	
	@Override
	public abstract boolean equals(Object obj);
	
	public abstract double length();
	
	public abstract double lengthSquared();
	
	public void normalize() {
		scale(1 / length());
	}
	
	public abstract double angle(T vec);
	
	public abstract void cross(T vec1, T vec2);
	
	public abstract double dot(T vec);
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < dimensions(); i++) {
			if (i > 0)
				builder.append(",");
			builder.append(get(i));
		}
		builder.append("]");
		return builder.toString();
	}
	
}
