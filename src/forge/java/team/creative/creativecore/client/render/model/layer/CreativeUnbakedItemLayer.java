package team.creative.creativecore.client.render.model.layer;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public interface CreativeUnbakedItemLayer {
    
    void resolveParents(Function<ResourceLocation, UnbakedModel> getter);
    
    @Nullable
    CreativeBakedItemLayer bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state);
    
    public static class CreativeUnbakedItemLayerWrapper implements CreativeUnbakedItemLayer {
        
        public final BlockModel model;
        
        public CreativeUnbakedItemLayerWrapper(JsonObject object) {
            this.model = BlockModel.fromString(object.toString());
        }
        
        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> getter) {
            model.resolveParents(getter);
        }
        
        @Override
        public CreativeBakedItemLayer bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
            return new CreativeBakedItemLayer.CreativeBakedItemLayerWrapper(model.bake(baker, model, spriteGetter, state, true));
        }
        
    }
    
}
