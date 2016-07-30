package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import com.creativemd.creativecore.common.block.TileEntityState;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
				
				Block block = renderBlock;
				if(cube.block != null)
					block = cube.block;
				IBlockState newState = null;
				if(cube.meta != -1)
					newState = block.getStateFromMeta(cube.meta);
				else
					newState = block.getDefaultState();
				TextureAtlasSprite texture = mc.getBlockRendererDispatcher().getModelForState(newState).getParticleTexture();;
				HashMap<EnumFacing, BakedQuad> quads = new HashMap<>();
				Map<EnumFacing, BlockPartFace> mapFacesIn = new HashMap<>();
				
				for (EnumFacing facing : EnumFacing.values()) {
					mapFacesIn.put(facing, new BlockPartFace(facing, -1, "", new BlockFaceUV(null, 0)));
				}
				
				BlockPart blockPart = new BlockPart(new Vector3f(cube.minX*16F, cube.minY*16F, cube.minZ*16F),
						new Vector3f(cube.maxX*16F, cube.maxY*16F, cube.maxZ*16F), mapFacesIn, null, true);
				
				if(baseState == null)
					loadTransformation();
				for (EnumFacing facing : EnumFacing.values()) {
					quads.put(facing, makeBakedQuad(blockPart, mapFacesIn.get(facing), texture, facing, baseState, false));
				}
				
				if(side == null)
				{
					for (BakedQuad bakedQuad : quads.values()) {
						baked.add(bakedQuad);
					}
				}
				else
					baked.add(quads.get(side));
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
