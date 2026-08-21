package team.creative.creativecore.client.render.model.layer;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

public record CreativeUnbakedLayerModel(List<CreativeUnbakedItemLayer> layers) implements IUnbakedGeometry<CreativeUnbakedLayerModel> {
    
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        Material particleLocation = context.getMaterial("particle");
        TextureAtlasSprite particle = spriteGetter.apply(particleLocation);
        
        var rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity())
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        
        var layers = ImmutableList.<CreativeBakedItemLayer>builder();
        for (CreativeUnbakedItemLayer layer : this.layers)
            layers.add(layer.bake(baker, spriteGetter, modelState));
        
        return new CreativeBakedLayerModel(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, context.getTransforms(), overrides, layers.build());
    }
    
    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        layers.forEach(child -> child.resolveParents(modelGetter));
    }
    
}
