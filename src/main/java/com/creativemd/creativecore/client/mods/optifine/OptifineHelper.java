package com.creativemd.creativecore.client.mods.optifine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class OptifineHelper {
	
	private static boolean active = FMLClientHandler.instance().hasOptifine();
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	private static boolean ltInstalled = Loader.isModLoaded("littletiles");
	
	private static ThreadLocal<Object> renderEnv;
	
	private static boolean loadedOptifineReflections = false;
	private static Method getCustomizedQuads;
	private static Method getCustomizedModel;
	public static Class renderEnvClass;
	private static Class blockModelCustomizer;
	private static Constructor<?> renderEnvConstructor;
	private static Method resetEnv;
	private static Method getEnv;
	private static Method getAoFace;
	private static Method getColorMultiplier;
	private static Field isEmissive;
	private static Method getQuadEmissive;
	private static boolean newVersion = false;
	
	private static void loadOptifineReflections() {
		try {
			loadedOptifineReflections = true;
			renderEnvClass = Class.forName("net.optifine.render.RenderEnv");
			try {
				renderEnvConstructor = renderEnvClass.getConstructor(IBlockAccess.class, IBlockState.class, BlockPos.class);
				resetEnv = ReflectionHelper.findMethod(renderEnvClass, "reset", "reset", IBlockAccess.class, IBlockState.class, BlockPos.class);
				getEnv = ReflectionHelper.findMethod(BufferBuilder.class, "getRenderEnv", "getRenderEnv", IBlockAccess.class, IBlockState.class, BlockPos.class);
			} catch (NoSuchMethodException e) {
				newVersion = true;
				renderEnvConstructor = renderEnvClass.getConstructor(IBlockState.class, BlockPos.class);
				resetEnv = ReflectionHelper.findMethod(renderEnvClass, "reset", "reset", IBlockState.class, BlockPos.class);
				getEnv = ReflectionHelper.findMethod(BufferBuilder.class, "getRenderEnv", "getRenderEnv", IBlockState.class, BlockPos.class);
			}
			getAoFace = ReflectionHelper.findMethod(renderEnvClass, "getAoFace", "getAoFace");
			blockModelCustomizer = Class.forName("net.optifine.model.BlockModelCustomizer");
			getCustomizedModel = ReflectionHelper.findMethod(blockModelCustomizer, "getRenderModel", "getRenderModel", IBakedModel.class, IBlockState.class, renderEnvClass);
			getCustomizedQuads = ReflectionHelper.findMethod(blockModelCustomizer, "getRenderQuads", "getRenderQuads", List.class, IBlockAccess.class, IBlockState.class, BlockPos.class, EnumFacing.class, BlockRenderLayer.class, long.class, renderEnvClass);
			Class customColorsClass = Class.forName("net.optifine.CustomColors");
			getColorMultiplier = ReflectionHelper.findMethod(customColorsClass, "getColorMultiplier", "getColorMultiplier", BakedQuad.class, IBlockState.class, IBlockAccess.class, BlockPos.class, renderEnvClass);
			
			renderEnv = ThreadLocal.withInitial(new Supplier<Object>() {
				
				@Override
				public Object get() {
					try {
						if (newVersion)
							return renderEnvConstructor.newInstance(null, null);
						return renderEnvConstructor.newInstance(null, null, null);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			});
			
			Class configClass = Class.forName("Config");
			isShadersMethod = configClass.getMethod("isShaders");
			isRenderRegions = configClass.getMethod("isRenderRegions");
			
			regionX = ReflectionHelper.findField(RenderChunk.class, "regionX");
			regionZ = ReflectionHelper.findField(RenderChunk.class, "regionZ");
			
			isEmissive = ReflectionHelper.findField(TextureAtlasSprite.class, "isEmissive");
			
			getQuadEmissive = ReflectionHelper.findMethod(BakedQuad.class, "getQuadEmissive", "getQuadEmissive");
			
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			active = false;
			e.printStackTrace();
		}
	}
	
	public static IBakedModel getRenderModel(IBakedModel model, IBlockAccess world, IBlockState state, BlockPos pos) {
		if (!active)
			return model;
		try {
			return (IBakedModel) getCustomizedModel.invoke(null, model, state, getEnv(world, state, pos));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static Object getEnv(BufferBuilder builder, IBlockAccess world, IBlockState state, BlockPos pos) {
		try {
			if (newVersion)
				return getEnv.invoke(builder, state, pos);
			return getEnv.invoke(builder, world, state, pos);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getEnv(IBlockAccess world, IBlockState state, BlockPos pos) {
		try {
			Object env = renderEnv.get();
			if (newVersion)
				resetEnv.invoke(env, state, pos);
			else
				resetEnv.invoke(env, world, state, pos);
			return env;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static List<BakedQuad> getRenderQuads(List<BakedQuad> quads, IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing facing, BlockRenderLayer layer, long rand) {
		if (!active || world == null || layer == null)
			return quads;
		try {
			if (ltInstalled)
				quads = (List<BakedQuad>) getCustomizedQuads.invoke(null, quads, world, state, pos, facing, layer, rand, getEnv(world, state, pos));
			
			int size = quads.size();
			for (int i = 0; i < size; i++) {
				BakedQuad emissive = (BakedQuad) getQuadEmissive.invoke(quads.get(i));
				if (emissive != null)
					quads.add(emissive);
			}
			
			return quads;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static int getColorMultiplier(BakedQuad quad, IBlockState state, IBlockAccess world, BlockPos pos) {
		try {
			return (int) getColorMultiplier.invoke(null, quad, state, world, pos, getEnv(world, state, pos));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static Object getAoFace(Object renderEnv) {
		try {
			return getAoFace.invoke(renderEnv);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isActive() {
		return active;
	}
	
	private static Method isShadersMethod = null;
	
	public static boolean isShaders() {
		try {
			return (boolean) isShadersMethod.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static Method isRenderRegions = null;
	
	public static boolean isRenderRegions() {
		try {
			return (boolean) isRenderRegions.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static Field regionX;
	
	public static int getRenderChunkRegionX(RenderChunk chunk) {
		try {
			return regionX.getInt(chunk);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	private static Field regionZ;
	
	public static int getRenderChunkRegionZ(RenderChunk chunk) {
		try {
			return regionZ.getInt(chunk);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static boolean isEmissive(TextureAtlasSprite sprite) {
		if (!active)
			return false;
		
		try {
			return isEmissive.getBoolean(sprite);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static BakedQuad getQuadEmissive(BakedQuad quad) {
		if (!active)
			return quad;
		
		try {
			BakedQuad emissive = (BakedQuad) getQuadEmissive.invoke(quad);
			if (emissive != null)
				return emissive;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return quad;
	}
	
	static {
		if (active)
			loadOptifineReflections();
	}
	
}
