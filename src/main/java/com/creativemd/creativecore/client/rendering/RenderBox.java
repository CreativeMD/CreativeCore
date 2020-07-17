package com.creativemd.creativecore.client.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.box.AlignedBox;
import com.creativemd.creativecore.common.utils.math.vec.VectorFan;
import com.creativemd.creativecore.common.utils.mc.BlockUtils;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBox extends AlignedBox {
	
	private static final VectorFan DOWN = new VectorFan(new Vector3f[] { new Vector3f(0, 0, 1), new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 0, 1) });
	private static final VectorFan UP = new VectorFan(new Vector3f[] { new Vector3f(0, 1, 0), new Vector3f(0, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, 0) });
	private static final VectorFan NORTH = new VectorFan(new Vector3f[] { new Vector3f(1, 1, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0) });
	private static final VectorFan SOUTH = new VectorFan(new Vector3f[] { new Vector3f(0, 1, 1), new Vector3f(0, 0, 1), new Vector3f(1, 0, 1), new Vector3f(1, 1, 1) });
	private static final VectorFan WEST = new VectorFan(new Vector3f[] { new Vector3f(0, 1, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, 1, 1) });
	private static final VectorFan EAST = new VectorFan(new Vector3f[] { new Vector3f(1, 1, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0, 0), new Vector3f(1, 1, 0) });
	
	public static enum SideRenderType {
		
		INSIDE_RENDERED(true, false), INSIDE_NOT_RENDERED(false, false), OUTSIDE_RENDERED(true, true), OUTSIDE_NOT_RENDERD(false, true);
		
		public final boolean shouldBeRendered;
		public final boolean outside;
		
		SideRenderType(boolean shouldBeRendered, boolean outside) {
			this.shouldBeRendered = shouldBeRendered;
			this.outside = outside;
		}
		
	}
	
	public Block block;
	public int meta = 0;
	public int color = -1;
	
	public Object customData = null;
	
	public boolean keepVU = false;
	public boolean allowOverlap = false;
	
	private SideRenderType renderEast = SideRenderType.INSIDE_RENDERED;
	private SideRenderType renderWest = SideRenderType.INSIDE_RENDERED;
	private SideRenderType renderUp = SideRenderType.INSIDE_RENDERED;
	private SideRenderType renderDown = SideRenderType.INSIDE_RENDERED;
	private SideRenderType renderSouth = SideRenderType.INSIDE_RENDERED;
	private SideRenderType renderNorth = SideRenderType.INSIDE_RENDERED;
	
	private Object quadEast = null;
	private Object quadWest = null;
	private Object quadUp = null;
	private Object quadDown = null;
	private Object quadSouth = null;
	private Object quadNorth = null;
	
	public boolean doesNeedQuadUpdate = true;
	
	public RenderBox(AlignedBox cube, RenderBox box) {
		super(cube);
		this.block = box.block;
		this.meta = box.meta;
		this.color = box.color;
		this.renderEast = box.renderEast;
		this.renderWest = box.renderWest;
		this.renderUp = box.renderUp;
		this.renderDown = box.renderDown;
		this.renderSouth = box.renderSouth;
		this.renderNorth = box.renderNorth;
	}
	
	public RenderBox(AlignedBox cube, Block block, int meta) {
		super(cube);
		this.block = block;
		this.meta = meta;
	}
	
	public RenderBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
	}
	
	public RenderBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Block block, int meta) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.block = block;
		this.meta = meta;
	}
	
	public RenderBox setColor(int color) {
		this.color = color;
		return this;
	}
	
	public RenderBox setKeepUV(boolean keep) {
		this.keepVU = keep;
		return this;
	}
	
	public void setQuad(EnumFacing facing, List<BakedQuad> quads) {
		Object quad = quads == null || quads.isEmpty() ? null : quads.size() == 1 ? quads.get(0) : quads;
		switch (facing) {
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
	
	public Object getQuad(EnumFacing facing) {
		switch (facing) {
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
	
	public int countQuads() {
		int quads = 0;
		if (quadUp != null)
			quads += quadUp instanceof List ? ((List) quadUp).size() : 1;
		if (quadDown != null)
			quads += quadDown instanceof List ? ((List) quadDown).size() : 1;
		if (quadEast != null)
			quads += quadEast instanceof List ? ((List) quadEast).size() : 1;
		if (quadWest != null)
			quads += quadWest instanceof List ? ((List) quadWest).size() : 1;
		if (quadSouth != null)
			quads += quadSouth instanceof List ? ((List) quadSouth).size() : 1;
		if (quadNorth != null)
			quads += quadNorth instanceof List ? ((List) quadNorth).size() : 1;
		return quads;
	}
	
	public IBlockState getModelState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return block.getExtendedState(state, world, pos);
	}
	
	public IBlockState getBlockState() {
		if (meta != -1)
			return BlockUtils.getState(block, meta);
		else
			return block.getDefaultState();
	}
	
	public IBlockState getBlockState(Block block) {
		if (meta != -1)
			return BlockUtils.getState(block, meta);
		else
			return block.getDefaultState();
	}
	
	public void setType(EnumFacing facing, SideRenderType renderer) {
		switch (facing) {
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
	
	public SideRenderType getType(EnumFacing facing) {
		switch (facing) {
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
		return SideRenderType.INSIDE_RENDERED;
	}
	
	public boolean renderSide(EnumFacing facing) {
		switch (facing) {
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
	
	public boolean intersectsWithFace(EnumFacing facing, RenderInformationHolder holder, BlockPos offset) {
		switch (facing.getAxis()) {
		case X:
			return holder.maxY > this.minY - offset.getY() && holder.minY < this.maxY - offset.getY() && holder.maxZ > this.minZ - offset.getZ() && holder.minZ < this.maxZ - offset.getZ();
		case Y:
			return holder.maxX > this.minX - offset.getX() && holder.minX < this.maxX - offset.getX() && holder.maxZ > this.minZ - offset.getZ() && holder.minZ < this.maxZ - offset.getZ();
		case Z:
			return holder.maxX > this.minX - offset.getX() && holder.minX < this.maxX - offset.getX() && holder.maxY > this.minY - offset.getY() && holder.minY < this.maxY - offset.getY();
		}
		return false;
	}
	
	public boolean isEmissive = false;
	
	protected Object getRenderQuads(EnumFacing facing) {
		switch (facing) {
		case DOWN:
			return DOWN;
		case EAST:
			return EAST;
		case NORTH:
			return NORTH;
		case SOUTH:
			return SOUTH;
		case UP:
			return UP;
		case WEST:
			return WEST;
		}
		return null;
	}
	
	protected List<BakedQuad> getBakedQuad(IBlockAccess world, IBakedModel blockModel, IBlockState state, EnumFacing facing, BlockPos pos, BlockRenderLayer layer, long rand) {
		return OptifineHelper.getRenderQuads(blockModel.getQuads(state, facing, rand), world, state, pos, facing, layer, rand);
	}
	
	protected float getOffsetX() {
		return minX;
	}
	
	protected float getOffsetY() {
		return minY;
	}
	
	protected float getOffsetZ() {
		return minZ;
	}
	
	protected float getScaleX() {
		return maxX - minX;
	}
	
	protected float getScaleY() {
		return maxY - minY;
	}
	
	protected float getScaleZ() {
		return maxZ - minZ;
	}
	
	protected boolean scaleAndOffsetQuads() {
		return true;
	}
	
	public List<BakedQuad> getBakedQuad(IBlockAccess world, @Nullable BlockPos pos, BlockPos offset, IBlockState state, IBakedModel blockModel, EnumFacing facing, BlockRenderLayer layer, long rand, boolean overrideTint, int defaultColor) {
		List<BakedQuad> blockQuads = getBakedQuad(world, blockModel, state, facing, pos, layer, rand);
		
		if (blockQuads.isEmpty())
			return Collections.emptyList();
		
		RenderInformationHolder holder = new RenderInformationHolder(facing, this.color != -1 ? this.color : defaultColor);
		holder.offset = offset;
		
		List<BakedQuad> quads = new ArrayList<>();
		for (int i = 0; i < blockQuads.size(); i++) {
			holder.setQuad(blockQuads.get(i), overrideTint, defaultColor);
			
			if (!isEmissive && OptifineHelper.isEmissive(holder.quad.getSprite()))
				isEmissive = true;
			
			VertexFormat format = holder.quad.getFormat();
			int[] data = holder.quad.getVertexData();
			
			int index = 0;
			int uvIndex = index + format.getUvOffsetById(0) / 4;
			float tempMinX = Float.intBitsToFloat(data[index]);
			float tempMinY = Float.intBitsToFloat(data[index + 1]);
			float tempMinZ = Float.intBitsToFloat(data[index + 2]);
			
			float tempU = Float.intBitsToFloat(data[uvIndex]);
			float tempV = Float.intBitsToFloat(data[uvIndex + 1]);
			
			holder.uvInverted = false;
			
			index = 1 * format.getIntegerSize();
			uvIndex = index + format.getUvOffsetById(0) / 4;
			if (tempMinX != Float.intBitsToFloat(data[index])) {
				if (tempU != Float.intBitsToFloat(data[uvIndex]))
					holder.uvInverted = Axis.X != RotationUtils.getUAxisFromFacing(facing);
				else
					holder.uvInverted = Axis.X != RotationUtils.getVAxisFromFacing(facing);
			} else if (tempMinY != Float.intBitsToFloat(data[index + 1])) {
				if (tempU != Float.intBitsToFloat(data[uvIndex]))
					holder.uvInverted = Axis.Y != RotationUtils.getUAxisFromFacing(facing);
				else
					holder.uvInverted = Axis.Y != RotationUtils.getVAxisFromFacing(facing);
			} else {
				if (tempU != Float.intBitsToFloat(data[uvIndex]))
					holder.uvInverted = Axis.Z != RotationUtils.getUAxisFromFacing(facing);
				else
					holder.uvInverted = Axis.Z != RotationUtils.getVAxisFromFacing(facing);
			}
			
			index = 2 * format.getIntegerSize();
			float tempMaxX = Float.intBitsToFloat(data[index]);
			float tempMaxY = Float.intBitsToFloat(data[index + 1]);
			float tempMaxZ = Float.intBitsToFloat(data[index + 2]);
			
			holder.setBounds(tempMinX, tempMinY, tempMinZ, tempMaxX, tempMaxY, tempMaxZ);
			
			// Check if it is intersecting, otherwise there is no need to render it
			if (!intersectsWithFace(facing, holder, offset))
				continue;
			
			uvIndex = format.getUvOffsetById(0) / 4;
			float u1 = Float.intBitsToFloat(data[uvIndex]);
			float v1 = Float.intBitsToFloat(data[uvIndex + 1]);
			uvIndex = 2 * format.getIntegerSize() + format.getUvOffsetById(0) / 4;
			float u2 = Float.intBitsToFloat(data[uvIndex]);
			float v2 = Float.intBitsToFloat(data[uvIndex + 1]);
			
			if (holder.uvInverted) {
				holder.sizeU = RotationUtils.getVFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getVFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? u2 - u1 : u1 - u2;
				holder.sizeV = RotationUtils.getUFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getUFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? v2 - v1 : v1 - v2;
			} else {
				holder.sizeU = RotationUtils.getUFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getUFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? u2 - u1 : u1 - u2;
				holder.sizeV = RotationUtils.getVFromFacing(facing, tempMinX, tempMinY, tempMinZ) < RotationUtils.getVFromFacing(facing, tempMaxX, tempMaxY, tempMaxZ) ? v2 - v1 : v1 - v2;
			}
			
			Object renderQuads = getRenderQuads(holder.facing);
			if (renderQuads instanceof List)
				for (int j = 0; j < ((List<VectorFan>) renderQuads).size(); j++)
					((List<VectorFan>) renderQuads).get(j).generate(holder, quads);
			else if (renderQuads instanceof VectorFan)
				((VectorFan) renderQuads).generate(holder, quads);
		}
		return quads;
		
	}
	
	public void deleteQuadCache() {
		doesNeedQuadUpdate = true;
		quadEast = null;
		quadWest = null;
		quadUp = null;
		quadDown = null;
		quadSouth = null;
		quadNorth = null;
	}
	
	protected void setupPreviewRendering(double x, double y, double z) {
		GlStateManager.translate(x + minX, y + minY, z + minZ);
		GlStateManager.scale(maxX - minX, maxY - minY, maxZ - minZ);
	}
	
	public void renderPreview(double x, double y, double z, int alpha) {
		int red = ColorUtils.getRed(color);
		int green = ColorUtils.getGreen(color);
		int blue = ColorUtils.getBlue(color);
		
		GlStateManager.pushMatrix();
		setupPreviewRendering(x, y, z);
		
		for (int i = 0; i < EnumFacing.VALUES.length; i++) {
			Object renderQuads = getRenderQuads(EnumFacing.VALUES[i]);
			if (renderQuads instanceof List)
				for (int j = 0; j < ((List<VectorFan>) renderQuads).size(); j++)
					((List<VectorFan>) renderQuads).get(j).renderPreview(red, green, blue, alpha);
			else if (renderQuads instanceof VectorFan)
				((VectorFan) renderQuads).renderPreview(red, green, blue, alpha);
		}
		GlStateManager.popMatrix();
	}
	
	public void renderLines(double x, double y, double z, int alpha) {
		int red = ColorUtils.getRed(color);
		int green = ColorUtils.getGreen(color);
		int blue = ColorUtils.getBlue(color);
		
		if (red == 1 && green == 1 && blue == 1)
			red = green = blue = 0;
		
		GlStateManager.pushMatrix();
		if (scaleAndOffsetQuads()) {
			GlStateManager.translate(x + minX, y + minY, z + minZ);
			GlStateManager.scale(maxX - minX, maxY - minY, maxZ - minZ);
		} else
			GlStateManager.translate(x, y, z);
		
		for (int i = 0; i < EnumFacing.VALUES.length; i++) {
			Object renderQuads = getRenderQuads(EnumFacing.VALUES[i]);
			if (renderQuads instanceof List)
				for (int j = 0; j < ((List<VectorFan>) renderQuads).size(); j++)
					((List<VectorFan>) renderQuads).get(j).renderLines(red, green, blue, alpha);
			else if (renderQuads instanceof VectorFan)
				((VectorFan) renderQuads).renderLines(red, green, blue, alpha);
		}
		GlStateManager.popMatrix();
	}
	
	public class RenderInformationHolder {
		
		public final EnumFacing facing;
		public final int color;
		public BlockPos offset;
		public boolean shouldOverrideColor;
		
		public BakedQuad quad;
		
		public final boolean scaleAndOffset;
		
		public final float offsetX;
		public final float offsetY;
		public final float offsetZ;
		
		public final float scaleX;
		public final float scaleY;
		public final float scaleZ;
		
		public float minX;
		public float minY;
		public float minZ;
		public float maxX;
		public float maxY;
		public float maxZ;
		
		public float sizeX;
		public float sizeY;
		public float sizeZ;
		
		public boolean uvInverted;
		public float sizeU;
		public float sizeV;
		
		public RenderInformationHolder(EnumFacing facing, int color) {
			this.color = color;
			this.facing = facing;
			RenderBox box = getBox();
			scaleAndOffset = box.scaleAndOffsetQuads();
			if (scaleAndOffset) {
				this.offsetX = box.getOffsetX();
				this.offsetY = box.getOffsetY();
				this.offsetZ = box.getOffsetZ();
				this.scaleX = box.getScaleX();
				this.scaleY = box.getScaleY();
				this.scaleZ = box.getScaleZ();
			} else {
				this.offsetX = 0;
				this.offsetY = 0;
				this.offsetZ = 0;
				this.scaleX = 0;
				this.scaleY = 0;
				this.scaleZ = 0;
			}
		}
		
		public void setQuad(BakedQuad quad, boolean overrideTint, int defaultColor) {
			this.quad = quad;
			this.shouldOverrideColor = overrideTint && (defaultColor == -1 || quad.hasTintIndex()) && color != -1;
		}
		
		public void setBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
			this.minX = Math.min(minX, maxX);
			this.minY = Math.min(minY, maxY);
			this.minZ = Math.min(minZ, maxZ);
			this.maxX = Math.max(minX, maxX);
			this.maxY = Math.max(minY, maxY);
			this.maxZ = Math.max(minZ, maxZ);
			
			this.sizeX = this.maxX - this.minX;
			this.sizeY = this.maxY - this.minY;
			this.sizeZ = this.maxZ - this.minZ;
		}
		
		public RenderBox getBox() {
			return RenderBox.this;
		}
	}
	
}
