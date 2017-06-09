package com.creativemd.creativecore.common.utils;

import java.util.ArrayList;

import net.minecraft.util.math.AxisAlignedBB;

public class BoxUtils {
	
	public static boolean equals(double a, double b, double deviation)
	{
	    return a == b ? true : Math.abs(a - b) < deviation;
	}
	
	public static boolean greaterEquals(double a, double b, double deviation)
	{
		return a >= (b > 0 ? b - deviation : b + deviation);
	}
	
	
	/**
	 * It is used to improve performance
	 * @param will contain the new compressed boxes
	 * @param deviation if zero this will be 100% accurate, otherwise it will try to compromise the boxes
	 */
	public static void compressBoxes(ArrayList<AxisAlignedBB> boxes, double deviation)
	{
		int size = 0;
		while(size != boxes.size())
		{
			size = boxes.size();
			int i = 0;
			while(i < boxes.size()){
				int j = 0;
				while(j < boxes.size()) {
					if(i != j)
					{
						AxisAlignedBB box = combineBoxes(boxes.get(i), boxes.get(j), deviation);
						if(box != null)
						{
							boxes.set(i, box);
							boxes.remove(j);
							if(i > j)
								i--;
							continue;
						}
					}
					j++;
				}
				i++;
			}
		}
	}
	
	public static AxisAlignedBB sumBox(AxisAlignedBB box1, AxisAlignedBB box2)
	{
		return new AxisAlignedBB(Math.min(box1.minX, box2.minX), Math.min(box1.minY, box2.minY), Math.min(box1.minZ, box2.minZ), Math.max(box1.maxX, box2.maxX), Math.max(box1.maxY, box2.maxY), Math.max(box1.maxZ, box2.maxZ));
	}
	
	public static AxisAlignedBB combineBoxes(AxisAlignedBB box1, AxisAlignedBB box2, double deviation)
	{
		if(deviation == 0)
		{
			boolean x = box1.minX == box2.minX && box1.maxX == box2.maxX;
			boolean y = box1.minY == box2.minY && box1.maxY == box2.maxY;
			boolean z = box1.minZ == box2.minZ && box1.maxZ == box2.maxZ;
			
			if(x && y && z)
			{
				return box1;
			}
			if(x && y)
			{
				if(box1.maxZ >= box2.minZ && box1.minZ <= box2.maxZ)
					return new AxisAlignedBB(box1.minX, box1.minY, Math.min(box1.minZ, box2.minZ), box1.maxX, box1.maxY, Math.max(box1.maxZ, box2.maxZ));
			}
			if(x && z)
			{
				if(box1.maxY >= box2.minY && box1.minY <= box2.maxY)
					return new AxisAlignedBB(box1.minX, Math.min(box1.minY, box2.minY), box1.minZ, box1.maxX, Math.max(box1.maxY, box2.maxY), box1.maxZ);
			}
			if(y && z)
			{
				if(box1.maxX >= box2.minX && box1.minX <= box2.maxX)
					return new AxisAlignedBB(Math.min(box1.minX, box2.minX), box1.minY, box1.minZ, Math.max(box1.maxX, box2.maxX), box1.maxY, box1.maxZ);
			}
			return null;
		}else{
			boolean x = equals(box1.minX, box2.minX, deviation) && equals(box1.maxX, box2.maxX, deviation);
			boolean y = equals(box1.minY, box2.minY, deviation) && equals(box1.maxY, box2.maxY, deviation);
			boolean z = equals(box1.minZ, box2.minZ, deviation) && equals(box1.maxZ, box2.maxZ, deviation);
			
			if(x && y && z)
				return sumBox(box1, box2);
			
			if(x && y && greaterEquals(box1.maxZ, box2.minZ, deviation) && greaterEquals(box2.maxZ, box1.minZ, deviation))
				return sumBox(box1, box2);
			
			if(x && z && greaterEquals(box1.maxY, box2.minY, deviation) && greaterEquals(box2.maxY, box1.minY, deviation))
				return sumBox(box1, box2);
			
			if(y && z && greaterEquals(box1.maxX, box2.minX, deviation) && greaterEquals(box2.maxX, box1.minX, deviation))
				return sumBox(box1, box2);
			
			return null;
		}
	}
}
