package com.creativemd.creativecore.common.utils;

import org.lwjgl.util.Color;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class ColorUtils {
	
	public static int WHITE = 16777215;
	
	public static int RGBAToInt(Color color)
	{
		return ((int)color.getAlpha() & 255) << 24 | ((int)color.getRed() & 255) << 16 | ((int)color.getGreen() & 255) << 8 | (int)color.getBlue() & 255;
	}
	
	public static Color IntToRGBA(int color)
	{
		int a = color >> 24 & 255;
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        return new Color(r, g, b, a);
	}
	
	public static int RGBToInt(Vec3i color)
	{
		return ((int)color.getX() & 255) << 16 | ((int)color.getY() & 255) << 8 | (int)color.getZ() & 255;
	}
	
	public static Vec3i IntToRGB(int color)
	{
		float r = (float)(color >> 16 & 255);
        float g = (float)(color >> 8 & 255);
        float b = (float)(color & 255);
        return new Vec3i(r, g, b);
	}
	
	public static Vec3d IntToVec(int color)
	{
		float r = (float)(color >> 16 & 255);
        float g = (float)(color >> 8 & 255);
        float b = (float)(color & 255);
        return new Vec3d(r/255F, g/255F, b/255F);
	}
	
	public static int VecToInt(Vec3d color)
	{
		return RGBToInt(new Vec3i(color.xCoord*255, color.yCoord*255, color.zCoord*255));
	}

	public static Vec3i colorToVec(Color color) {
		return new Vec3i(color.getRed(), color.getGreen(), color.getBlue());
	}
}
