package com.creativemd.creativecore.common.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCubeObject extends CubeObject {
	
	public static enum EnumSideRender {
		
		INSIDE_RENDERED(true, false),
		INSIDE_NOT_RENDERED(false, false),
		OUTSIDE_RENDERED(true, true),
		OUTSIDE_NOT_RENDERD(false, true);
		
		public final boolean shouldBeRendered;
		public final boolean outside;
		
		EnumSideRender(boolean shouldBeRendered, boolean outside)
		{
			this.shouldBeRendered = shouldBeRendered;
			this.outside = outside;
		}
		
	}
	
	public Block block;
	public int meta = 0;
	public int color = -1;
	
	public Object customData = null;
	
	public boolean keepVU = false;
	
	public EnumSideRender renderEast = EnumSideRender.INSIDE_RENDERED;
	public EnumSideRender renderWest = EnumSideRender.INSIDE_RENDERED;
	public EnumSideRender renderUp = EnumSideRender.INSIDE_RENDERED;
	public EnumSideRender renderDown = EnumSideRender.INSIDE_RENDERED;
	public EnumSideRender renderSouth = EnumSideRender.INSIDE_RENDERED;
	public EnumSideRender renderNorth = EnumSideRender.INSIDE_RENDERED;
	
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
	
	public IBlockState getBlockState()
	{
		if(meta != -1)
			return block.getStateFromMeta(meta);
		else
			return block.getDefaultState();
	}
	
	public IBlockState getBlockState(Block block)
	{
		if(meta != -1)
			return block.getStateFromMeta(meta);
		else
			return block.getDefaultState();
	}
	
	public void setSideRender(EnumFacing facing, EnumSideRender renderer)
	{
		switch(facing)
		{
		case DOWN:
			renderDown = renderer;
			break;
		case EAST:
			renderEast = renderer;
			break;
		case NORTH:
			renderNorth = renderer;
			break;
		case SOUTH:
			renderSouth = renderer;
			break;
		case UP:
			renderUp = renderer;
			break;
		case WEST:
			renderWest = renderer;
			break;
		}
	}
	
	public EnumSideRender getSidedRendererType(EnumFacing facing)
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
		return EnumSideRender.INSIDE_RENDERED;
	}
	
	public boolean shouldSideBeRendered(EnumFacing facing)
	{
		switch(facing)
		{
		case DOWN:
			return renderDown.shouldBeRendered;
		case EAST:
			return renderEast.shouldBeRendered;
		case NORTH:
			return renderNorth.shouldBeRendered;
		case SOUTH:
			return renderSouth.shouldBeRendered;
		case UP:
			return renderUp.shouldBeRendered;
		case WEST:
			return renderWest.shouldBeRendered;
		}
		return true;
	}
	
}
