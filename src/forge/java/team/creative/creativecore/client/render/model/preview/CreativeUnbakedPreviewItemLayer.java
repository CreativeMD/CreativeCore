package team.creative.creativecore.client.render.model.preview;

import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.client.render.model.layer.CreativeUnbakedItemLayer;

public class CreativeUnbakedPreviewItemLayer implements CreativeUnbakedItemLayer {
    
    public ItemModelPreview model;
    
    public CreativeUnbakedPreviewItemLayer(JsonObject object) {
        this.model = CreativeCoreClient.PREVIEW_MODEL_TYPES.getOrThrow(ResourceLocation.parse(object.get("item").getAsString()));
    }
    
    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> getter) {}
    
    @Override
    public CreativeBakedPreviewItemLayer bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
        return new CreativeBakedPreviewItemLayer(model);
    }
    
}
