package com.creativemd.creativecore.common.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

public class RenderCubeObject extends CubeObject {
	
	public Block block;
	public int meta = 0;
	public int color = -1;
	
	public boolean renderEast = true;
	public boolean renderWest = true;
	public boolean renderUp = true;
	public boolean renderDown = true;
	public boolean renderSouth = true;
	public boolean renderNorth = true;
	
	public RenderCubeObject(CubeObject cube, RenderCubeObject cube2)
	{
		super(cube);
		applyExtraCubeData(cube2);
	}
	
	public RenderCubeObject(CubeObject cube, Block block, int meta)
	{
		super(cube);
		this.block = block;
		this.meta = meta;
	}
	
	public RenderCubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block)
	{
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
	}
	
	public RenderCubeObject(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block, int meta)
	{
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
		this.meta = meta;
	}
	
	protected void applyExtraCubeData(CubeObject cube)
	{
		if(cube instanceof RenderCubeObject)
		{
			this.block = ((RenderCubeObject) cube).block;
			this.meta = ((RenderCubeObject) cube).meta;
			this.color = ((RenderCubeObject) cube).color;
			this.renderEast = ((RenderCubeObject) cube).renderEast;
			this.renderWest = ((RenderCubeObject) cube).renderWest;
			this.renderUp = ((RenderCubeObject) cube).renderUp;
			this.renderDown = ((RenderCubeObject) cube).renderDown;
			this.renderSouth = ((RenderCubeObject) cube).renderSouth;
			this.renderNorth = ((RenderCubeObject) cube).renderNorth;
		}
	}
	
	public RenderCubeObject setColor(Vec3i color)
	{
		this.setColor(ColorUtils.RGBToInt(color));
		return this;
	}
	
	public RenderCubeObject setColor(int color)
	{
		this.color = color;
		return this;
	}
	
	public IBlockState getBlockState(Block block)
	{
		if(meta != -1)
			return block.getStateFromMeta(meta);
		else
			return block.getDefaultState();
	}
	
	public void setSideRender(EnumFacing facing, boolean shouldBeRendered)
	{
		switch(facing)
		{
		case DOWN:
			renderDown = shouldBeRendered;
			break;
		case EAST:
			renderEast = shouldBeRendered;
			break;
		case NORTH:
			renderNorth = shouldBeRendered;
			break;
		case SOUTH:
			renderSouth = shouldBeRendered;
			break;
		case UP:
			renderUp = shouldBeRendered;
			break;
		case WEST:
			renderWest = shouldBeRendered;
			break;
		}
	}
	
	public boolean shouldSideBeRendered(EnumFacing facing)
	{
		switch(facing)
		{
		case DOWN:
			return renderDown;
		case EAST:
			return renderEast;
		case NORTH:
			return renderNorth;
		case SOUTH:
			return renderSouth;
		case UP:
			return renderUp;
		case WEST:
			return renderWest;
		}
		return true;
	}
	
}
