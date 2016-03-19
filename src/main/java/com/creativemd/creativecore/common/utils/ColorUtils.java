package com.creativemd.creativecore.common.utils;

import net.minecraft.util.math.Vec3d;

public class ColorUtils {
	
	public static int WHITE = 16777215;
	
	public static int RGBToInt(Vec3d color)
	{
		return ((int)color.xCoord & 255) << 16 | ((int)color.yCoord & 255) << 8 | (int)color.zCoord & 255;
	}
	
	public static Vec3d IntToRGB(int color)
	{
		float r = (float)(color >> 16 & 255);
        float g = (float)(color >> 8 & 255);
        float b = (float)(color & 255);
        return new Vec3d(r, g, b);
	}
}
