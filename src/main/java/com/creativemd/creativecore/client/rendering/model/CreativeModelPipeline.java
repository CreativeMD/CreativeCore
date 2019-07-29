package com.creativemd.creativecore.client.rendering.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.List;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;
import com.creativemd.creativecore.client.rendering.RenderCubeObject;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.common.utils.type.SingletonList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CreativeModelPipeline {
	
	public static BlockModelRenderer renderer;
	private static BlockColors blockColors;
	
	private static Class ambientOcclusionFaceClass;
	private static Constructor ambientOcclusionFaceClassConstructor;
	private static Method renderQuadsSmoothMethod;
	private static Method renderQuadsFlatMethod;
	private static Field vertexColorMultiplierField;
	
	static {
		try {
			renderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
			blockColors = Minecraft.getMinecraft().getBlockColors();
			for (Class<?> clazz : BlockModelRenderer.class.getDeclaredClasses())
				if (clazz.getName().equals("net.minecraft.client.renderer.BlockModelRenderer$AmbientOcclusionFace")) {
					ambientOcclusionFaceClass = clazz;
					break;
				}
			ambientOcclusionFaceClassConstructor = ambientOcclusionFaceClass.getConstructor(BlockModelRenderer.class);
			ambientOcclusionFaceClassConstructor.setAccessible(true);
			vertexColorMultiplierField = ReflectionHelper.findField(ambientOcclusionFaceClass, "vertexColorMultiplier", "field_178206_b");
			
			if (FMLClientHandler.instance().hasOptifine()) {
				renderQuadsSmoothMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsSmooth", "renderQuadsSmooth", IBlockAccess.class, IBlockState.class, BlockPos.class, BufferBuilder.class, List.class, OptifineHelper.renderEnvClass);
				renderQuadsFlatMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsFlat", "renderQuadsFlat", IBlockAccess.class, IBlockState.class, BlockPos.class, int.class, boolean.class, BufferBuilder.class, List.class, OptifineHelper.renderEnvClass);
			} else {
				renderQuadsSmoothMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsSmooth", "func_187492_a", IBlockAccess.class, IBlockState.class, BlockPos.class, BufferBuilder.class, List.class, float[].class, BitSet.class, ambientOcclusionFaceClass);
				renderQuadsFlatMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsFlat", "func_187496_a", IBlockAccess.class, IBlockState.class, BlockPos.class, int.class, boolean.class, BufferBuilder.class, List.class, BitSet.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object createAmbientOcclusionFace() {
		try {
			return ambientOcclusionFaceClassConstructor.newInstance(renderer);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Could not create ambient occlusion face!");
	}
	
	public static ThreadLocal<SingletonList<BakedQuad>> singletonList = ThreadLocal.withInitial(() -> new SingletonList(null));
	
	public static void renderBlockFaceSmooth(IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, BlockRenderLayer layer, List<BakedQuad> quads, float[] afloat, EnumFacing facing, BitSet set, Object ambientOcclusionFace, RenderCubeObject cube) {
		try {
			SingletonList<BakedQuad> list = singletonList.get();
			for (int i = 0; i < quads.size(); i++) {
				List<BakedQuad> singleQuad = quads instanceof SingletonList ? quads : list.setElement(quads.get(i));
				if (FMLClientHandler.instance().hasOptifine())
					renderQuadsSmoothMethod.invoke(renderer, world, state, pos, buffer, singleQuad, ambientOcclusionFace);
				else
					renderQuadsSmoothMethod.invoke(renderer, world, state, pos, buffer, singleQuad, afloat, set, ambientOcclusionFace);
				
				overwriteColor(world, state, pos, buffer, layer, singleQuad, cube, ambientOcclusionFace);
			}
			list.setElement(null);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void renderBlockFaceFlat(IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, BlockRenderLayer layer, List<BakedQuad> quads, EnumFacing facing, BitSet set, RenderCubeObject cube) {
		int light = state.getPackedLightmapCoords(world, pos.offset(facing));
		try {
			SingletonList<BakedQuad> list = singletonList.get();
			for (int i = 0; i < quads.size(); i++) {
				List<BakedQuad> singleQuad = quads instanceof SingletonList ? quads : list.setElement(quads.get(i));
				if (FMLClientHandler.instance().hasOptifine())
					renderQuadsFlatMethod.invoke(renderer, world, state, pos, light, false, buffer, singleQuad, OptifineHelper.getEnv(buffer, world, state, pos));
				else
					renderQuadsFlatMethod.invoke(renderer, world, state, pos, light, false, buffer, singleQuad, set);
				
				overwriteColor(world, state, pos, buffer, layer, singleQuad, cube, null);
			}
			list.setElement(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void overwriteColor(IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, BlockRenderLayer layer, List<BakedQuad> quads, RenderCubeObject cube, Object ambientOcclusionFace) {
		if (FMLClientHandler.instance().hasOptifine() && ambientOcclusionFace != null)
			ambientOcclusionFace = OptifineHelper.getAoFace(ambientOcclusionFace);
		
		float[] vertexColorMultiplier = null;
		try {
			vertexColorMultiplier = ambientOcclusionFace != null ? (float[]) vertexColorMultiplierField.get(ambientOcclusionFace) : null;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		VertexFormat format = buffer.getVertexFormat();
		;
		int vertexIndex = quads.size() * 4;
		for (int i = 0; i < quads.size(); i++) {
			vertexIndex -= 4;
			BakedQuad quad = quads.get(i);
			int tint = quad.getTintIndex();
			int multiplier = -1;
			if (OptifineHelper.isActive()) {
				multiplier = OptifineHelper.getColorMultiplier(quad, state, world, pos);
				if (multiplier != -1)
					tint = 0;
			}
			if (tint != -1 && (cube.color == -1 || ColorUtils.isWhite(cube.color))) {
				if (multiplier == -1)
					multiplier = blockColors.colorMultiplier(state, world, pos, tint);
				
				Color tempColor = ColorUtils.IntToRGBA(multiplier);
				if (cube.color != -1)
					tempColor.setAlpha(ColorUtils.getAlpha(cube.color));
				else
					tempColor.setAlpha(255);
				multiplier = ColorUtils.RGBAToInt(tempColor);
			} else {
				if (layer == BlockRenderLayer.CUTOUT_MIPPED && state.getBlock().getBlockLayer() != BlockRenderLayer.CUTOUT_MIPPED)
					tint = 0;
				if (layer != BlockRenderLayer.CUTOUT_MIPPED || state.getBlock().canRenderInLayer(state, BlockRenderLayer.CUTOUT))
					tint = 0;
				multiplier = cube.color;
				
				if (EntityRenderer.anaglyphEnable)
					multiplier = TextureUtil.anaglyphColor(multiplier);
			}
			if (tint != -1) {
				float r = (multiplier >> 16 & 255) / 255F;
				float g = (multiplier >> 8 & 255) / 255F;
				float b = (multiplier & 255) / 255F;
				int a = ColorUtils.getAlpha(multiplier);
				
				if (vertexColorMultiplier != null) {
					buffer.putColorRGBA(buffer.getColorIndex(vertexIndex + 1), (int) (vertexColorMultiplier[0] * r * 255), (int) (vertexColorMultiplier[0] * g * 255), (int) (vertexColorMultiplier[0] * b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(vertexIndex + 2), (int) (vertexColorMultiplier[1] * r * 255), (int) (vertexColorMultiplier[1] * g * 255), (int) (vertexColorMultiplier[1] * b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(vertexIndex + 3), (int) (vertexColorMultiplier[2] * r * 255), (int) (vertexColorMultiplier[2] * g * 255), (int) (vertexColorMultiplier[2] * b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(vertexIndex + 4), (int) (vertexColorMultiplier[3] * r * 255), (int) (vertexColorMultiplier[3] * g * 255), (int) (vertexColorMultiplier[3] * b * 255), a);
				} else {
					if (quad.shouldApplyDiffuseLighting()) {
						float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quad.getFace());
						r *= diffuse;
						g *= diffuse;
						b *= diffuse;
					}
					buffer.putColorRGBA(buffer.getColorIndex(vertexIndex + 1), (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(vertexIndex + 2), (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(vertexIndex + 3), (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(vertexIndex + 4), (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
				}
			}
		}
	}
}
