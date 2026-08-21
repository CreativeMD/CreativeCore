package team.creative.creativecore.client.render.model.box;

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

public class CreativeUnbakedBoxItemLayer implements CreativeUnbakedItemLayer {
    
    public static final ResourceLocation BLOCK_LOCATION = ResourceLocation.tryBuild("minecraft", "block/block");
    
    public ItemModelBox model;
    
    public CreativeUnbakedBoxItemLayer(JsonObject object) {
        this.model = CreativeCoreClient.BOX_MODEL_TYPES.getOrThrow(ResourceLocation.parse(object.get("item").getAsString()));
    }
    
    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> getter) {}
    
    @Override
    public CreativeBakedBoxItemLayer bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
        return new CreativeBakedBoxItemLayer(model, baker.bake(BLOCK_LOCATION, state, spriteGetter));
    }
    
}
