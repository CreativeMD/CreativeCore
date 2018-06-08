package com.creativemd.creativecore.common.utils;

import org.lwjgl.util.Color;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class ColorUtils {
	
	public static enum ColorPart {
		RED {
			@Override
			public int getColor(Color color) {
				return color.getRed();
			}

			@Override
			public void setColor(Color color, int intenstiy) {
				color.setRed(intenstiy);
			}

			@Override
			public int getBrightest() {
				return 0xFF0000;
			}
		}, GREEN {
			@Override
			public int getColor(Color color) {
				return color.getGreen();
			}

			@Override
			public void setColor(Color color, int intenstiy) {
				color.setGreen(intenstiy);
			}

			@Override
			public int getBrightest() {
				return 0x00FF00;
			}
		}, BLUE {
			@Override
			public int getColor(Color color) {
				return color.getBlue();
			}

			@Override
			public void setColor(Color color, int intenstiy) {
				color.setBlue(intenstiy);
			}

			@Override
			public int getBrightest() {
				return 0x0000FF;
			}
		}, ALPHA {
			@Override
			public int getColor(Color color) {
				return color.getAlpha();
			}

			@Override
			public void setColor(Color color, int intenstiy) {
				color.setAlpha(intenstiy);
			}

			@Override
			public int getBrightest() {
				return 0x000000FF;
			}
		};
		
		public abstract int getColor(Color color);
		
		public abstract void setColor(Color color, int intenstiy);
		
		public abstract int getBrightest();
	}
	
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

	public static Vec3i colorToVec(Color color)
	{
		return new Vec3i(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static boolean isWhite(int color)
	{
		int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
		return r == 255 && g == 255 && b == 255;
	}
	
	public static boolean isTransparent(int color)
	{
		int a = color >> 24 & 255;
		return a < 255;
	}
	
	public static boolean isInvisible(int color)
	{
		int a = color >> 24 & 255;
		return a == 0;
	}
	
	public static int blend(int i1, int i2)
	{
		return blend(i1, i2, 0.5F);
	}
	
	public static int blend(int i1, int i2, float ratio )
	{
	    if ( ratio > 1f ) ratio = 1f;
	    else if ( ratio < 0f ) ratio = 0f;
	    float iRatio = 1.0f - ratio;

	    int a1 = (i1 >> 24 & 0xff);
	    int r1 = ((i1 & 0xff0000) >> 16);
	    int g1 = ((i1 & 0xff00) >> 8);
	    int b1 = (i1 & 0xff);

	    int a2 = (i2 >> 24 & 0xff);
	    int r2 = ((i2 & 0xff0000) >> 16);
	    int g2 = ((i2 & 0xff00) >> 8);
	    int b2 = (i2 & 0xff);

	    int a = (int)((a1 * iRatio) + (a2 * ratio));
	    int r = (int)((r1 * iRatio) + (r2 * ratio));
	    int g = (int)((g1 * iRatio) + (g2 * ratio));
	    int b = (int)((b1 * iRatio) + (b2 * ratio));

	    return a << 24 | r << 16 | g << 8 | b;
	}
}
