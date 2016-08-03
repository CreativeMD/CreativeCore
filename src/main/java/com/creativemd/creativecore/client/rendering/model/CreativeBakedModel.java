package com.creativemd.creativecore.client.rendering.model;

import java.time.chrono.MinguoEra;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;

import com.creativemd.creativecore.common.block.TileEntityState;
import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.EnumFaceDirection.VertexInformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class CreativeBakedModel implements IBakedModel, IPerspectiveAwareModel {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static FaceBakery faceBakery = new FaceBakery();
	public static TextureAtlasSprite woodenTexture;
	
	private static ItemStack lastItemStack = null;
	
	public static ItemOverrideList customOverride = new ItemOverrideList(new ArrayList<>()){
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
	    {
	        lastItemStack = stack;
			return super.handleItemState(originalModel, stack, world, entity);
	    }
	};
	
	public static TextureAtlasSprite getWoodenTexture()
	{
		if(woodenTexture == null)
			woodenTexture = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/planks_oak");
		return woodenTexture;
	}
	
	protected static BakedQuad makeBakedQuad(BlockPart blockPart, BlockPartFace blockFace, TextureAtlasSprite texture, EnumFacing facing, net.minecraftforge.common.model.ITransformation transformation, boolean uvLocked)
    {
        return faceBakery.makeBakedQuad(blockPart.positionFrom, blockPart.positionTo, blockFace, texture, facing, transformation, blockPart.partRotation, uvLocked, blockPart.shade);
    }
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		ArrayList<BakedQuad> baked = new ArrayList<>();
		Block renderBlock = null;
		
		if(state == null && lastItemStack != null)
		{
			renderBlock = Block.getBlockFromItem(lastItemStack.getItem());
		}else if(state != null)
			renderBlock = state.getBlock();
		if(renderBlock instanceof ICreativeRendered)
		{
			TileEntity te = state instanceof TileEntityState ? ((TileEntityState) state).te : null;
			ArrayList<CubeObject> cubes = ((ICreativeRendered)renderBlock).getRenderingCubes(state, te, state != null ? null : lastItemStack);
			for (int i = 0; i < cubes.size(); i++) {
				CubeObject cube = cubes.get(i);
				//CubeObject invCube = new CubeObject(cube.minX, cube.minY, cube.minZ, cube.maxX-1, cube.maxY-1, cube.maxZ-1);
				
				Block block = renderBlock;
				if(cube.block != null)
					block = cube.block;
				
				
				
				//int overridenTint = -1;
				//if(lastItemStack != null)
					//overridenTint = mc.getItemColors().getColorFromItemstack(new ItemStack(block), -1);
				
				IBlockState newState = cube.getBlockState(block);
				if(state != null)
					newState = newState.getActualState(te.getWorld(), te.getPos()) ;
				
				BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
				if(layer != null && !block.canRenderInLayer(newState, layer))
					continue;
				
				IBakedModel blockModel = mc.getBlockRendererDispatcher().getModelForState(newState);
				List<BakedQuad> blockQuads = blockModel.getQuads(newState, side, rand);
				for (int j = 0; j < blockQuads.size(); j++) {
					BakedQuad quad = new CreativeBakedQuad(blockQuads.get(j), cube, -1);
					
					EnumFacing facing = side;
					if(facing == null)
						facing = faceBakery.getFacingFromVertexData(quad.getVertexData());
					
					EnumFaceDirection direction = EnumFaceDirection.getFacing(facing);
					
					/*float scaleX = 1;
					float scaleY = 1;
					float scaleZ = 1;
					
					int lastIndexX = -1;
					int lastIndexY = -1;
					int lastIndexZ = -1;
					for (int k = 0; k < 4; k++) {
						VertexInformation vertex = direction.getVertexInformation(k);
						int index = k * quad.getFormat().getIntegerSize();
						float newX = cube.getVertexInformationPosition(vertex.xIndex);
						float newY = cube.getVertexInformationPosition(vertex.yIndex);
						float newZ = cube.getVertexInformationPosition(vertex.zIndex);
						
						float oldX = Float.intBitsToFloat(quad.getVertexData()[index]);
						float oldY = Float.intBitsToFloat(quad.getVertexData()[index+1]);
						float oldZ = Float.intBitsToFloat(quad.getVertexData()[index+2]);
						
						
					}*/
					
					for (int k = 0; k < 4; k++) {
						VertexInformation vertex = direction.getVertexInformation(k);
						
						int index = k * quad.getFormat().getIntegerSize();
						float newX = cube.getVertexInformationPosition(vertex.xIndex);
						float newY = cube.getVertexInformationPosition(vertex.yIndex);
						float newZ = cube.getVertexInformationPosition(vertex.zIndex);
						
						/*float oldX = Float.intBitsToFloat(quad.getVertexData()[index]);
						float oldY = Float.intBitsToFloat(quad.getVertexData()[index+1]);
						float oldZ = Float.intBitsToFloat(quad.getVertexData()[index+2]);*/
						
						quad.getVertexData()[index] = Float.floatToIntBits(newX);
						quad.getVertexData()[index+1] = Float.floatToIntBits(newY);
						quad.getVertexData()[index+2] = Float.floatToIntBits(newZ);
					}
					
					baked.add(quad);
				}
			}
		}
		return baked;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		
		return getWoodenTexture();
	}
	
	private static ImmutableMap<TransformType, TRSRTransformation> cameraTransforms;
	private static TRSRTransformation baseState;

	public static final void loadTransformation()
	{
		Map<TransformType, TRSRTransformation> tMap = Maps.newHashMap();
        tMap.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(ItemCameraTransforms.DEFAULT));
        tMap.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(ModelRotation.X0_Y0));
        IModelState perState = new SimpleModelState(ImmutableMap.copyOf(tMap));
        baseState = perState.apply(Optional.<IModelPart>absent()).or(TRSRTransformation.identity());
        cameraTransforms = ImmutableMap.copyOf(tMap);
	}
	
	@Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
    {
		if(cameraTransforms == null)
			loadTransformation();
        //return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType);
		Pair<? extends IBakedModel, Matrix4f> pair = ((IPerspectiveAwareModel) mc.getBlockRendererDispatcher().getModelForState(Blocks.PLANKS.getDefaultState())).handlePerspective(cameraTransformType);
		return pair.of(this, pair.getRight());
    }
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return customOverride;
	}

}
