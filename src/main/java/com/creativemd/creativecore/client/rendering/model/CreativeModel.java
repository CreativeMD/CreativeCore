package com.creativemd.creativecore.client.rendering.model;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
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
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
    
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return bakedModel;
    }
    
}
