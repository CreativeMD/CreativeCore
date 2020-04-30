package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.creativemd.creativecore.client.rendering.RenderBox;
import com.creativemd.creativecore.common.block.TileEntityState;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class CreativeBakedModel implements IBakedModel {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static ItemColors itemColores = null;
	
	public static void lateInit() {
		itemColores = mc.getItemColors();
	}
	
	// public static FaceBakery faceBakery = new FaceBakery();
	public static TextureAtlasSprite woodenTexture;
	
	private static ItemStack lastItemStack = null;
	
	public static void setLastItemStack(ItemStack stack) {
		lastItemStack = stack;
	}
	
	public static ItemOverrideList customOverride = new ItemOverrideList(new ArrayList<>()) {
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			lastItemStack = stack;
			return super.handleItemState(originalModel, stack, world, entity);
		}
	};
	
	public static TextureAtlasSprite getWoodenTexture() {
		if (woodenTexture == null)
			woodenTexture = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/planks_oak");
		return woodenTexture;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return getBlockQuads(state, side, rand, false);
	}
	
	public static List<BakedQuad> getBlockQuads(List<? extends RenderBox> cubes, List<BakedQuad> baked, ICreativeRendered renderer, EnumFacing side, IBlockState state, BlockRenderLayer layer, Block renderBlock, TileEntity te, long rand, ItemStack stack, boolean threaded) {
		for (int i = 0; i < cubes.size(); i++) {
			RenderBox cube = cubes.get(i);
			
			if (!cube.renderSide(side))
				continue;
			
			Block block = renderBlock;
			if (cube.block != null)
				block = cube.block;
			
			IBlockState newState = cube.getBlockState(block);
			if (state != null && te != null)
				newState = newState.getActualState(te.getWorld(), te.getPos());
			
			try {
				if (layer != null && renderBlock != null && !renderBlock.canRenderInLayer(state, layer))
					continue;
			} catch (Exception e) {
				try {
					if (block.getBlockLayer() != layer)
						continue;
				} catch (Exception e2) {
					if (layer != BlockRenderLayer.SOLID)
						continue;
				}
			}
			
			IBakedModel blockModel = mc.getBlockRendererDispatcher().getModelForState(newState);
			
			int defaultColor = -1;
			if (te == null && stack != null)
				defaultColor = itemColores.colorMultiplier(new ItemStack(newState.getBlock(), 1, newState.getBlock().getMetaFromState(newState)), -1);
			
			baked.addAll(cube.getBakedQuad(te != null ? te.getWorld() : null, te != null ? te.getPos() : null, cube.getOffset(), newState, blockModel, side, layer, rand, true, defaultColor));
		}
		
		if (baked.size() > 0 && renderer != null)
			renderer.saveCachedModel(side, layer, baked, state, te, stack, threaded);
		return baked;
	}
	
	public static List<BakedQuad> getBakedQuad(IBlockAccess world, RenderBox cube, @Nullable BlockPos pos, BlockPos offset, IBlockState state, IBakedModel blockModel, BlockRenderLayer layer, EnumFacing side, long rand, boolean overrideTint) {
		return cube.getBakedQuad(world, pos, offset, state, blockModel, side, layer, rand, overrideTint, -1);
	}
	
	public static List<BakedQuad> getBlockQuads(IBlockState state, EnumFacing side, long rand, boolean threaded) {
		ArrayList<BakedQuad> baked = new ArrayList<>();
		
		Block renderBlock = null;
		
		if (state == null && lastItemStack != null) {
			renderBlock = Block.getBlockFromItem(lastItemStack.getItem());
		} else if (state != null)
			renderBlock = state.getBlock();
		
		TileEntity te = state instanceof TileEntityState ? ((TileEntityState) state).te : null;
		ItemStack stack = state != null ? null : lastItemStack;
		
		List<? extends RenderBox> cubes = null;
		
		ICreativeRendered renderer = null;
		if (renderBlock instanceof ICreativeRendered)
			renderer = (ICreativeRendered) renderBlock;
		else if (lastItemStack != null && lastItemStack.getItem() instanceof ICreativeRendered)
			renderer = (ICreativeRendered) lastItemStack.getItem();
		
		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
		
		if (side == null)
			return baked;
		
		if (renderer != null) {
			List<BakedQuad> cached = renderer.getCachedModel(side, layer, state, te, stack, threaded);
			if (cached != null) {
				return cached;
			}
			cubes = renderer.getRenderingCubes(state, te, stack);
		}
		
		if (cubes != null) {
			return getBlockQuads(cubes, baked, renderer, side, state, layer, renderBlock, te, rand, stack, threaded);
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
	
	private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
		return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null));
	}
	
	private static TRSRTransformation leftify(TRSRTransformation transform) {
		return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
	}
	
	private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);
	
	public static final void loadTransformation() {
		TRSRTransformation thirdperson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
		ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(TransformType.GUI, get(0, 0, 0, 30, 225, 0, 0.625f));
		builder.put(TransformType.GROUND, get(0, 3, 0, 0, 0, 0, 0.25f));
		builder.put(TransformType.FIXED, get(0, 0, 0, 0, 0, 0, 0.5f));
		builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
		builder.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
		builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
		builder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 0, 0, 225, 0, 0.4f));
		IModelState perState = new SimpleModelState(ImmutableMap.copyOf(builder.build()));
		baseState = perState.apply(Optional.empty()).orElse(TRSRTransformation.identity());
		cameraTransforms = ImmutableMap.copyOf(builder.build());
	}
	
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		if (cameraTransforms == null)
			loadTransformation();
		
		if (lastItemStack != null) {
			ICreativeRendered renderer = null;
			Block block = Block.getBlockFromItem(lastItemStack.getItem());
			if (block instanceof ICreativeRendered)
				renderer = (ICreativeRendered) block;
			else if (lastItemStack != null && lastItemStack.getItem() instanceof ICreativeRendered)
				renderer = (ICreativeRendered) lastItemStack.getItem();
			
			if (renderer != null)
				renderer.applyCustomOpenGLHackery(lastItemStack, cameraTransformType);
		}
		
		TRSRTransformation tr = cameraTransforms.get(cameraTransformType);
		Matrix4f mat = null;
		if (tr != null && !tr.equals(TRSRTransformation.identity()))
			mat = TRSRTransformation.blockCornerToCenter(tr).getMatrix();
		return Pair.of(this, mat);
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
