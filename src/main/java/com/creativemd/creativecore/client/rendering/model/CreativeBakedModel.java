package com.creativemd.creativecore.client.rendering.model;

import java.time.chrono.MinguoEra;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.conn.routing.RouteInfo.LayerType;
import org.lwjgl.util.Color;

import com.creativemd.creativecore.client.rendering.RenderCubeObject;
import com.creativemd.creativecore.common.block.TileEntityState;
import com.creativemd.creativecore.common.utils.math.box.CubeObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.GlStateManager;
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
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.RegistryDelegate;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CreativeBakedModel implements IBakedModel, IPerspectiveAwareModel {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static ItemColors itemColores = mc.getItemColors();
	//public static FaceBakery faceBakery = new FaceBakery();
	public static TextureAtlasSprite woodenTexture;
	
	private static ItemStack lastItemStack = null;
	
	public static void setLastItemStack(ItemStack stack)
	{
		lastItemStack = stack;
	}
	
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
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return getBlockQuads(state, side, rand, false);
	}
	
	public static boolean doesBlockHaveColor(Block block)
	{
		if(block.getBlockLayer() == BlockRenderLayer.CUTOUT_MIPPED)
			return true;
		Map<RegistryDelegate<Block>, IBlockColor> blockColorMap = ReflectionHelper.getPrivateValue(BlockColors.class, mc.getBlockColors(), "blockColorMap");
		return blockColorMap.containsKey(block.delegate);
	}
	
	public static List<BakedQuad> getBlockQuads(List<? extends RenderCubeObject> cubes, List<BakedQuad> baked, ICreativeRendered renderer, EnumFacing side, IBlockState state, BlockRenderLayer layer, Block renderBlock, TileEntity te, long rand, ItemStack stack, boolean threaded) {
		for (int i = 0; i < cubes.size(); i++) {
			RenderCubeObject cube = cubes.get(i);
			
			if(!cube.shouldSideBeRendered(side))
				continue;
			
			Block block = renderBlock;
			if(cube.block != null)
				block = cube.block;
			
			IBlockState newState = cube.getBlockState(block);
			if(state != null && te != null)
				newState = newState.getActualState(te.getWorld(), te.getPos());
			
			try{
				if(layer != null && renderBlock != null && !renderBlock.canRenderInLayer(state, layer))
					continue;
			}catch(Exception e){
				try{
					if(block.getBlockLayer() != layer)
						continue;
				}catch(Exception e2){
					if(layer != BlockRenderLayer.SOLID)
						continue;
				}
			}
			
			IBakedModel blockModel = mc.getBlockRendererDispatcher().getModelForState(newState);
			
			int defaultColor = -1;
			if(te == null && stack != null)
				defaultColor = itemColores.getColorFromItemstack(new ItemStack(newState.getBlock(), 1, newState.getBlock().getMetaFromState(newState)), -1);
			
			baked.addAll(cube.getBakedQuad(te != null ? te.getWorld() : null, te != null ? te.getPos() : null, cube.getOffset(), newState, blockModel, side, layer, rand, true, defaultColor));
		}
		
		if(baked.size() > 0)
			renderer.saveCachedModel(side,layer, baked, state, te, stack, threaded);
		return baked;
	}
	
	public static List<BakedQuad> getBakedQuad(IBlockAccess world, RenderCubeObject cube, @Nullable BlockPos pos, BlockPos offset, IBlockState state, IBakedModel blockModel, BlockRenderLayer layer, EnumFacing side, long rand, boolean overrideTint)
	{
		return cube.getBakedQuad(world, pos, offset, state, blockModel, side, layer, rand, overrideTint, -1);
	}
	
	/*public static List<BakedQuad> getBakedQuad(RenderCubeObject cube, CubeObject uvCube, IBlockState state, IBakedModel blockModel, EnumFacing side, long rand, boolean overrideTint, int defaultColor)
	{
		List<BakedQuad> blockQuads = blockModel.getQuads(state, side, rand);
		if(blockQuads.isEmpty())
			return Collections.emptyList();
		
		List<BakedQuad> quads = new ArrayList<>();
		
		int color = cube.color != -1 ? cube.color : defaultColor;
		for(int i = 0; i < blockQuads.size(); i++)
		{
			BakedQuad quad = new CreativeBakedQuad(blockQuads.get(i), cube, color, overrideTint && (defaultColor == -1 || blockQuads.get(i).hasTintIndex()) && color != -1, side);
			EnumFacing facing = side;
			
			EnumFaceDirection direction = EnumFaceDirection.getFacing(facing);
			
			for (int k = 0; k < 4; k++) {
				VertexInformation vertex = direction.getVertexInformation(k);
				
				int index = k * quad.getFormat().getIntegerSize();
				float newX = cube.getVertexInformationPosition(vertex.xIndex);
				float newY = cube.getVertexInformationPosition(vertex.yIndex);
				float newZ = cube.getVertexInformationPosition(vertex.zIndex);
				
				quad.getVertexData()[index] = Float.floatToIntBits(newX);
				quad.getVertexData()[index+1] = Float.floatToIntBits(newY);
				quad.getVertexData()[index+2] = Float.floatToIntBits(newZ);
				
				if(cube.keepVU)
					continue;
				
				int uvIndex = index + quad.getFormat().getUvOffsetById(0) / 4;
				
				newX = uvCube.getVertexInformationPosition(vertex.xIndex);
				newY = uvCube.getVertexInformationPosition(vertex.yIndex);
				newZ = uvCube.getVertexInformationPosition(vertex.zIndex);
				
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
	}*/
	
	public static List<BakedQuad> getBlockQuads(IBlockState state, EnumFacing side, long rand, boolean threaded) {
		//long time = System.nanoTime();
		ArrayList<BakedQuad> baked = new ArrayList<>();
		
		Block renderBlock = null;
		
		if(state == null && lastItemStack != null)
		{
			renderBlock = Block.getBlockFromItem(lastItemStack.getItem());
		}else if(state != null)
			renderBlock = state.getBlock();
		
		
		TileEntity te = state instanceof TileEntityState ? ((TileEntityState) state).te : null;
		ItemStack stack = state != null ? null : lastItemStack;
		
		List<? extends RenderCubeObject> cubes = null;
		
		ICreativeRendered renderer = null;
		if(renderBlock instanceof ICreativeRendered)
			renderer = (ICreativeRendered)renderBlock;
		else if(lastItemStack != null && lastItemStack.getItem() instanceof ICreativeRendered)
			renderer = (ICreativeRendered) lastItemStack.getItem();
		
		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
		
		if(side == null)
			return baked;
		
		if(renderer != null)
		{
			List<BakedQuad> cached = renderer.getCachedModel(side, layer, state, te, stack, threaded);
			if(cached != null)
			{
				//System.out.println("done in " + (System.nanoTime()-time));
				return cached;
			}
			cubes = renderer.getRenderingCubes(state, te, stack);
		}
			
		if(cubes != null)	
		{
			return getBlockQuads(cubes, baked, renderer, side, state, layer, renderBlock, te, rand, stack, threaded);
		}
		//System.out.println("Rerendered everything in " + (System.nanoTime()-time));
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
	
	private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s)
    {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(tx / 16, ty / 16, tz / 16),
            TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
            new Vector3f(s, s, s),
            null));
    }
	
	private static TRSRTransformation leftify(TRSRTransformation transform)
    {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }
	
	private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

	public static final void loadTransformation()
	{
		TRSRTransformation thirdperson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        builder.put(TransformType.GUI,                     get(0, 0, 0, 30, 225, 0, 0.625f));
        builder.put(TransformType.GROUND,                  get(0, 3, 0, 0, 0, 0, 0.25f));
        builder.put(TransformType.FIXED,                   get(0, 0, 0, 0, 0, 0, 0.5f));
        builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        builder.put(TransformType.THIRD_PERSON_LEFT_HAND,  leftify(thirdperson));
        builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
        builder.put(TransformType.FIRST_PERSON_LEFT_HAND,  get(0, 0, 0, 0, 225, 0, 0.4f));
        //ret.state = Optional.<IModelState>of(new SimpleModelState(builder.build()));
        IModelState perState = new SimpleModelState(ImmutableMap.copyOf(builder.build()));
        baseState = perState.apply(Optional.<IModelPart>absent()).or(TRSRTransformation.identity());
        cameraTransforms = ImmutableMap.copyOf(builder.build());
	}
	
	@Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
    {
		if(cameraTransforms == null)
			loadTransformation();
		
		if(lastItemStack != null)
		{
			ICreativeRendered renderer = null;
			Block block = Block.getBlockFromItem(lastItemStack.getItem());
			if(block instanceof ICreativeRendered)
				renderer = (ICreativeRendered)block;
			else if(lastItemStack != null && lastItemStack.getItem() instanceof ICreativeRendered)
				renderer = (ICreativeRendered) lastItemStack.getItem();
			
			if(renderer != null)
				renderer.applyCustomOpenGLHackery(lastItemStack, cameraTransformType);
		}
		
		//Pair<? extends IBakedModel, Matrix4f> pair = ((IPerspectiveAwareModel) mc.getBlockRendererDispatcher().getModelForState(Blocks.PLANKS.getDefaultState())).handlePerspective(cameraTransformType);
		TRSRTransformation tr = cameraTransforms.get(cameraTransformType);
        Matrix4f mat = null;
        if(tr != null && !tr.equals(TRSRTransformation.identity())) mat = TRSRTransformation.blockCornerToCenter(tr).getMatrix();
        return Pair.of(this, mat);
		//return Pair.of(this, cameraTransforms.get(cameraTransformType).getMatrix());
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
