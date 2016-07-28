package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.creativemd.creativecore.common.block.TileEntityState;
import com.creativemd.creativecore.common.utils.CubeObject;

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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class CreativeBakedModel implements IBakedModel {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static FaceBakery faceBakery = new FaceBakery();
	public static TextureAtlasSprite woodenTexture;
	
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
		
		if(state.getBlock() instanceof ICreativeRendered && state instanceof TileEntityState)
		{
			ArrayList<CubeObject> cubes = ((ICreativeRendered)state.getBlock()).getRenderingCubes(state, ((TileEntityState) state).te);
			for (int i = 0; i < cubes.size(); i++) {
				CubeObject cube = cubes.get(i);
				
				Block block = state.getBlock();
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
				for (EnumFacing facing : EnumFacing.values()) {
					quads.put(facing, makeBakedQuad(blockPart, mapFacesIn.get(facing), texture, facing, ModelRotation.X0_Y0, false));
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

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

}
