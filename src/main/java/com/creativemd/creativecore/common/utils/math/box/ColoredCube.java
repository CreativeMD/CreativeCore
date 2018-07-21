package com.creativemd.creativecore.common.utils.math.box;

import com.creativemd.creativecore.client.rendering.RenderCubeObject;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class ColoredCube extends CubeObject {
	
	public int color = -1;
	
	public ColoredCube(CubeObject cube)
	{
		this(cube, -1);
	}
	
	public ColoredCube(CubeObject cube, int color)
	{
		super(cube);
		setColor(color);
	}
	
	public ColoredCube(CubeObject cube, Vec3i color)
	{
		super(cube);
		setColor(color);
	}
	
	public ColoredCube(CubeObject cube, Vec3d color)
	{
		super(cube);
		setColor(color);
	}
	
	public ColoredCube setColor(Vec3d color)
	{
		this.setColor(ColorUtils.VecToInt(color));
		return this;
	}
	
	public ColoredCube setColor(Vec3i color)
	{
		this.setColor(ColorUtils.RGBToInt(color));
		return this;
	}
	
	public ColoredCube setColor(int color)
	{
		this.color = color;
		return this;
	}
	
	@Override
	protected void applyExtraCubeData(CubeObject cube)
	{
		if(cube instanceof ColoredCube)
		{
			this.color = ((ColoredCube) cube).color;
		}
	}
}
