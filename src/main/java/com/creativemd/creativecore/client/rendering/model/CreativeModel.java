package com.creativemd.creativecore.client.rendering.model;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class CreativeModel implements IModel {
	
	public static CreativeModel instance = new CreativeModel();
	public static CreativeBakedModel bakedModel = new CreativeBakedModel();
	
	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableSet.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableSet.of();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return bakedModel;
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

}
