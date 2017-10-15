package com.creativemd.creativecore.client.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.creativemd.creativecore.client.rendering.model.CreativeBakedQuad;
import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.common.utils.CubeObject;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.EnumFaceDirection.VertexInformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
	
	private EnumSideRender renderEast = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderWest = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderUp = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderDown = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderSouth = EnumSideRender.INSIDE_RENDERED;
	private EnumSideRender renderNorth = EnumSideRender.INSIDE_RENDERED;
	
	private List<BakedQuad> quadEast = null;
	private List<BakedQuad> quadWest = null;
	private List<BakedQuad> quadUp = null;
	private List<BakedQuad> quadDown = null;
	private List<BakedQuad> quadSouth = null;
	private List<BakedQuad> quadNorth = null;
	
	public boolean doesNeedQuadUpdate = true;
	
	public void setQuad(EnumFacing facing, List<BakedQuad> quad)
	{
		switch(facing)
		{
		case DOWN:
			quadDown = quad;
			break;
		case EAST:
			quadEast = quad;
			break;
		case NORTH:
			quadNorth = quad;
			break;
		case SOUTH:
			quadSouth = quad;
			break;
		case UP:
			quadUp = quad;
			break;
		case WEST:
			quadWest = quad;
			break;
		}
	}
	
	public List<BakedQuad> getQuad(EnumFacing facing)
	{
		switch(facing)
		{
		case DOWN:
			return quadDown;
		case EAST:
			return quadEast;
		case NORTH:
			return quadNorth;
		case SOUTH:
			return quadSouth;
		case UP:
			return quadUp;
		case WEST:
			return quadWest;
		}
		return null;
	}
	
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
	
	public List<BakedQuad> getBakedQuad(BlockPos offset, IBlockState state, IBakedModel blockModel, EnumFacing side, long rand, boolean overrideTint, int defaultColor)
	{
		List<BakedQuad> blockQuads = blockModel.getQuads(state, side, rand);
		if(blockQuads.isEmpty())
			return Collections.emptyList();
		
		List<BakedQuad> quads = new ArrayList<>();
		
		int color = this.color != -1 ? this.color : defaultColor;
		for(int i = 0; i < blockQuads.size(); i++)
		{
			BakedQuad quad = new CreativeBakedQuad(blockQuads.get(i), this, color, overrideTint && (defaultColor == -1 || blockQuads.get(i).hasTintIndex()) && color != -1, side);
			EnumFacing facing = side;
			
			EnumFaceDirection direction = EnumFaceDirection.getFacing(facing);
			
			for (int k = 0; k < 4; k++) {
				VertexInformation vertex = direction.getVertexInformation(k);
				
				int index = k * quad.getFormat().getIntegerSize();
				float newX = getVertexInformationPosition(vertex.xIndex);
				float newY = getVertexInformationPosition(vertex.yIndex);
				float newZ = getVertexInformationPosition(vertex.zIndex);
				
				quad.getVertexData()[index] = Float.floatToIntBits(newX);
				quad.getVertexData()[index+1] = Float.floatToIntBits(newY);
				quad.getVertexData()[index+2] = Float.floatToIntBits(newZ);
				
				if(keepVU)
					continue;
				
				int uvIndex = index + quad.getFormat().getUvOffsetById(0) / 4;
				
				newX = getVertexInformationPositionOffset(vertex.xIndex, offset);
				newY = getVertexInformationPositionOffset(vertex.yIndex, offset);
				newZ = getVertexInformationPositionOffset(vertex.zIndex, offset);
				
				float uMin = 0;
				float uMax = 1;
				float vMin = 0;
				float vMax = 1;
				
				float u = uMin;
				float v = vMin;
				switch(facing)
				{
				case EAST:
					newY = vMax-newY;
					newZ = uMax-newZ;
				case WEST:
					if(facing == EnumFacing.WEST)
						newY = vMax-newY;
					u = newZ;
					v = newY;
					break;
				case DOWN:
					newZ = vMax-newZ;
				case UP:
					u = newX;
					v = newZ;
					break;
				case NORTH:
					newY = vMax-newY;
					newX = uMax-newX;
				case SOUTH:
					if(facing == EnumFacing.SOUTH)
						newY = vMax-newY;
					u = newX;
					v = newY;
					break;
				}
				
				u *= 16;
				v *= 16;
				
				quad.getVertexData()[uvIndex] = Float.floatToRawIntBits(quad.getSprite().getInterpolatedU(u));
				quad.getVertexData()[uvIndex + 1] = Float.floatToRawIntBits(quad.getSprite().getInterpolatedV(v));
			}
			quads.add(quad);
		}
		return quads;
	}
	
}
