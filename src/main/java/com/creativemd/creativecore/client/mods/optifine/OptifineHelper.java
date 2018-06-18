package com.creativemd.creativecore.client.mods.optifine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class OptifineHelper {
	
	private static boolean active = FMLClientHandler.instance().hasOptifine();
	
	private static ThreadLocal<Object> renderEnv;
	
	private static boolean loadedOptifineReflections = false;
	private static Method getCustomizedQuads;
	private static Method getCustomizedModel;
	private static Class renderEnvClass;
	private static Class blockModelCustomizer;
	private static Constructor<?> renderEnvConstructor;
	private static Method resetEnv;
	
	private static void loadOptifineReflections()
	{
		try {
			loadedOptifineReflections = true;
			renderEnvClass = Class.forName("RenderEnv");
			renderEnvConstructor = renderEnvClass.getConstructor(IBlockAccess.class, IBlockState.class, BlockPos.class);
			resetEnv = ReflectionHelper.findMethod(renderEnvClass, "reset", "reset", IBlockAccess.class, IBlockState.class, BlockPos.class);
			blockModelCustomizer = Class.forName("BlockModelCustomizer");
			getCustomizedModel = ReflectionHelper.findMethod(blockModelCustomizer, "getRenderModel", "getRenderModel", IBakedModel.class, IBlockState.class, renderEnvClass);			
			getCustomizedQuads = ReflectionHelper.findMethod(blockModelCustomizer, "getRenderQuads", "getRenderQuads", List.class, IBlockAccess.class, IBlockState.class, BlockPos.class, EnumFacing.class, BlockRenderLayer.class, long.class, renderEnvClass);
			
			renderEnv = ThreadLocal.withInitial(new Supplier<Object>() {

				@Override
				public Object get() {
					try {
						return renderEnvConstructor.newInstance(null, null, null);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	private static Object getEnv(IBlockAccess world, IBlockState state, BlockPos pos)
	{
		try {
			Object env = renderEnv.get();
			resetEnv.invoke(env, world, state, pos);
			return env;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static List<BakedQuad> getRenderQuads(List<BakedQuad> quads, IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing facing, BlockRenderLayer layer, long rand)
	{
		if(!active || world == null || layer == null)
			return quads;
		try {
			return (List<BakedQuad>) getCustomizedQuads.invoke(null, quads, world, state, pos, facing, layer, rand, getEnv(world, state, pos));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	static
	{
		if(active)
			loadOptifineReflections();
	}
	
}
