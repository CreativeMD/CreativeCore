package com.creativemd.creativecore.client.rendering.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.List;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;
import com.creativemd.creativecore.client.rendering.RenderBox;
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
import net.minecraftforge.client.model.pipeline.BlockInfo;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public class CreativeModelPipeline {
	
	public static BlockModelRenderer renderer;
	private static BlockColors blockColors;
	
	private static Class ambientOcclusionFaceClass;
	private static Constructor ambientOcclusionFaceClassConstructor;
	private static Method renderQuadsSmoothMethod;
	private static Method renderQuadsFlatMethod;
	private static Field vertexColorMultiplierField;
	
	private static Field lighterFlatField;
	private static Field lighterSmoothField;
	private static Field wrFlatField;
	private static Field wrSmoothField;
	private static Method setBufferMethod;
	private static Field blockInfoField;
	private static Field fullBlockInfoField;
	
	static {
		try {
			renderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
			blockColors = Minecraft.getMinecraft().getBlockColors();
			for (Class<?> clazz : BlockModelRenderer.class.getDeclaredClasses())
				if (clazz.getName().equals("net.minecraft.client.renderer.BlockModelRenderer$AmbientOcclusionFace")) {
					ambientOcclusionFaceClass = clazz;
					break;
				}
			ambientOcclusionFaceClassConstructor = ReflectionHelper.findConstructor(ambientOcclusionFaceClass, BlockModelRenderer.class);
			vertexColorMultiplierField = ReflectionHelper.findField(ambientOcclusionFaceClass, new String[] { "vertexColorMultiplier", "field_178206_b" });
			
			if (FMLClientHandler.instance().hasOptifine()) {
				renderQuadsSmoothMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsSmooth", "renderQuadsSmooth", IBlockAccess.class, IBlockState.class, BlockPos.class, BufferBuilder.class, List.class, OptifineHelper.renderEnvClass);
				renderQuadsFlatMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsFlat", "renderQuadsFlat", IBlockAccess.class, IBlockState.class, BlockPos.class, int.class, boolean.class, BufferBuilder.class, List.class, OptifineHelper.renderEnvClass);
			} else {
				renderQuadsSmoothMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsSmooth", "func_187492_a", IBlockAccess.class, IBlockState.class, BlockPos.class, BufferBuilder.class, List.class, float[].class, BitSet.class, ambientOcclusionFaceClass);
				renderQuadsFlatMethod = ReflectionHelper.findMethod(BlockModelRenderer.class, "renderQuadsFlat", "func_187496_a", IBlockAccess.class, IBlockState.class, BlockPos.class, int.class, boolean.class, BufferBuilder.class, List.class, BitSet.class);
			}
			
			lighterFlatField = ReflectionHelper.findField(ForgeBlockModelRenderer.class, "lighterFlat");
			lighterSmoothField = ReflectionHelper.findField(ForgeBlockModelRenderer.class, "lighterSmooth");
			wrFlatField = ReflectionHelper.findField(ForgeBlockModelRenderer.class, new String[] { "wrFlat", "consumerFlat" });
			wrSmoothField = ReflectionHelper.findField(ForgeBlockModelRenderer.class, new String[] { "wrSmooth", "consumerSmooth" });
			try {
				setBufferMethod = ReflectionHelper.findMethod(VertexBufferConsumer.class, "setBuffer", "setBuffer", BufferBuilder.class);
			} catch (UnableToFindMethodException e) {
				setBufferMethod = null;
			}
			blockInfoField = ReflectionHelper.findField(VertexLighterFlat.class, "blockInfo");
			fullBlockInfoField = ReflectionHelper.findField(BlockInfo.class, "full");
			
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
	
	public static void renderBlockFaceSmooth(IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, BlockRenderLayer layer, List<BakedQuad> quads, float[] afloat, EnumFacing facing, BitSet set, Object ambientOcclusionFace, RenderBox cube) {
		try {
			SingletonList<BakedQuad> list = singletonList.get();
			
			if (ForgeModContainer.forgeLightPipelineEnabled) {
				ThreadLocal<VertexBufferConsumer> wrSmooth = (ThreadLocal<VertexBufferConsumer>) wrSmoothField.get(renderer);
				ThreadLocal<VertexLighterSmoothAo> lighterSmooth = (ThreadLocal<VertexLighterSmoothAo>) lighterSmoothField.get(renderer);
				
				VertexBufferConsumer newCons = wrSmooth.get();
				if (setBufferMethod == null) {
					newCons = new VertexBufferConsumer(buffer);
					wrSmooth.set(newCons);
				} else
					setBufferMethod.invoke(newCons, buffer);
				lighterSmooth.get().setParent(newCons);
				wrSmooth.get().setOffset(pos);
				
				VertexLighterSmoothAo lighter = lighterSmooth.get();
				lighter.setWorld(world);
				lighter.setState(state);
				lighter.setBlockPos(pos);
				lighter.updateBlockInfo();
				
				fullBlockInfoField.setBoolean(blockInfoField.get(lighter), false);
				
				for (int i = 0; i < quads.size(); i++) {
					quads.get(i).pipe(lighter);
					
					overwriteColor(world, state, pos, buffer, layer, quads.get(i), cube, null, lighter);
				}
			} else {
				for (int i = 0; i < quads.size(); i++) {
					SingletonList<BakedQuad> singleQuad = quads instanceof SingletonList ? (SingletonList<BakedQuad>) quads : list.setElement(quads.get(i));
					if (FMLClientHandler.instance().hasOptifine())
						renderQuadsSmoothMethod.invoke(renderer, world, state, pos, buffer, singleQuad, ambientOcclusionFace);
					else
						renderQuadsSmoothMethod.invoke(renderer, world, state, pos, buffer, singleQuad, afloat, set, ambientOcclusionFace);
					
					overwriteColor(world, state, pos, buffer, layer, singleQuad.get(0), cube, ambientOcclusionFace, null);
				}
				
			}
			list.setElement(null);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void renderBlockFaceFlat(IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, BlockRenderLayer layer, List<BakedQuad> quads, EnumFacing facing, BitSet set, RenderBox cube, Object renderEnv) {
		int light = state.getPackedLightmapCoords(world, pos.offset(facing));
		try {
			
			if (ForgeModContainer.forgeLightPipelineEnabled) {
				ThreadLocal<VertexBufferConsumer> wrFlat = (ThreadLocal<VertexBufferConsumer>) wrFlatField.get(renderer);
				ThreadLocal<VertexLighterFlat> lighterFlat = (ThreadLocal<VertexLighterFlat>) lighterFlatField.get(renderer);
				
				VertexBufferConsumer newCons = wrFlat.get();
				if (setBufferMethod == null) {
					newCons = new VertexBufferConsumer(buffer);
					wrFlat.set(newCons);
				} else
					setBufferMethod.invoke(newCons, buffer);
				lighterFlat.get().setParent(newCons);
				wrFlat.get().setOffset(pos);
				
				VertexLighterFlat lighter = lighterFlat.get();
				lighter.setWorld(world);
				lighter.setState(state);
				lighter.setBlockPos(pos);
				lighter.updateBlockInfo();
				
				fullBlockInfoField.setBoolean(blockInfoField.get(lighter), false);
				
				for (int i = 0; i < quads.size(); i++) {
					quads.get(i).pipe(lighter);
					
					overwriteColor(world, state, pos, buffer, layer, quads.get(i), cube, null, null);
				}
			} else {
				for (int i = 0; i < quads.size(); i++) {
					SingletonList<BakedQuad> list = singletonList.get();
					List<BakedQuad> singleQuad = quads instanceof SingletonList ? quads : list.setElement(quads.get(i));
					if (FMLClientHandler.instance().hasOptifine())
						renderQuadsFlatMethod.invoke(renderer, world, state, pos, light, false, buffer, singleQuad, renderEnv);
					else
						renderQuadsFlatMethod.invoke(renderer, world, state, pos, light, false, buffer, singleQuad, set);
					
					overwriteColor(world, state, pos, buffer, layer, singleQuad.get(0), cube, null, null);
					list.setElement(null);
				}
				
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void overwriteColor(IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, BlockRenderLayer layer, BakedQuad quad, RenderBox cube, Object ambientOcclusionFace, VertexLighterFlat lighter) {
		if (FMLClientHandler.instance().hasOptifine() && ambientOcclusionFace != null)
			ambientOcclusionFace = OptifineHelper.getAoFace(ambientOcclusionFace);
		
		float[] vertexColorMultiplier = null;
		try {
			vertexColorMultiplier = ambientOcclusionFace != null ? (float[]) vertexColorMultiplierField.get(ambientOcclusionFace) : null;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		VertexFormat format = buffer.getVertexFormat();
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
		if (tint != -1 && cube.color != -1) {
			float r = (multiplier >> 16 & 255) / 255F;
			float g = (multiplier >> 8 & 255) / 255F;
			float b = (multiplier & 255) / 255F;
			int a = ColorUtils.getAlpha(multiplier);
			
			if (vertexColorMultiplier != null || lighter != null) {
				if (ForgeModContainer.forgeLightPipelineEnabled) {
					try {
						BlockInfo info = (BlockInfo) blockInfoField.get(lighter);
						int oldMultiplier = info.getColorMultiplier(quad.getTintIndex());
						for (int i = 1; i <= 4; i++) {
							int exisiting = BufferBuilderUtils.get(buffer, buffer.getColorIndex(i));
							float ao = ColorUtils.getRed(oldMultiplier) > 0 ? ColorUtils.getRed(exisiting) / (float) ColorUtils.getRed(oldMultiplier) : (ColorUtils.getGreen(oldMultiplier) > 0 ? ColorUtils.getGreen(exisiting) / (float) ColorUtils.getGreen(oldMultiplier) : ColorUtils.getBlue(exisiting) / (float) ColorUtils.getBlue(oldMultiplier));
							if (!Float.isFinite(ao))
								ao = 1;
							buffer.putColorRGBA(buffer.getColorIndex(i), (int) (r * ao * 255), (int) (g * ao * 255), (int) (b * ao * 255), a);
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					
				} else {
					buffer.putColorRGBA(buffer.getColorIndex(4), (int) (vertexColorMultiplier[0] * r * 255), (int) (vertexColorMultiplier[0] * g * 255), (int) (vertexColorMultiplier[0] * b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(3), (int) (vertexColorMultiplier[1] * r * 255), (int) (vertexColorMultiplier[1] * g * 255), (int) (vertexColorMultiplier[1] * b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(2), (int) (vertexColorMultiplier[2] * r * 255), (int) (vertexColorMultiplier[2] * g * 255), (int) (vertexColorMultiplier[2] * b * 255), a);
					buffer.putColorRGBA(buffer.getColorIndex(1), (int) (vertexColorMultiplier[3] * r * 255), (int) (vertexColorMultiplier[3] * g * 255), (int) (vertexColorMultiplier[3] * b * 255), a);
				}
			} else {
				if (quad.shouldApplyDiffuseLighting()) {
					float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quad.getFace());
					r *= diffuse;
					g *= diffuse;
					b *= diffuse;
				}
				buffer.putColorRGBA(buffer.getColorIndex(4), (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
				buffer.putColorRGBA(buffer.getColorIndex(3), (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
				buffer.putColorRGBA(buffer.getColorIndex(2), (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
				buffer.putColorRGBA(buffer.getColorIndex(1), (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
			}
		}
	}
}
