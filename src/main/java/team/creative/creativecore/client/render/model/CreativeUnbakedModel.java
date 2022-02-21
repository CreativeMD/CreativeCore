package team.creative.creativecore.client.render.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class CreativeUnbakedModel implements IModelGeometry<CreativeUnbakedModel> {
    
    public static final CreativeUnbakedModel INSTANCE = new CreativeUnbakedModel();
    
    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        return CreativeBakedModel.INSTANCE;
    }
    
    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return Collections.EMPTY_LIST;
    }
    
}
