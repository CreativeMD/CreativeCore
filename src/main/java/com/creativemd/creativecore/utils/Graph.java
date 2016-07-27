package com.creativemd.creativecore.utils;

import java.util.HashMap;

public abstract class Graph {
	
	public final HashMap<Float, Float> points;
	
	public Graph(HashMap<Float, Float> points) {
		this.points = points;
	}
	
	public Float getPreviousPointX(float below)
	{
		Float next = null;
		for (Float point : points.keySet()) {
			if(point <= below && (next == null || point > next))
				next = point;
		}
		return next;
	}
	
	public Float getNextPointX(float above)
	{
		Float next = null;
		for (Float point : points.keySet()) {
			if(point >= above && (next == null || point < next))
				next = point;
		}
		return next;
	}
	
	public float getLastPointX()
	{
		return getPreviousPointX(Float.MAX_VALUE);
	}
	
	public float getFirstPointX()
	{
		return getNextPointX(Float.MIN_VALUE);
	}
	
	public abstract float getY(float x);
	
}
