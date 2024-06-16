package team.creative.creativecore.client.render.model;

import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import team.creative.creativecore.client.CreativeCoreClient;

public record CreativeUnbakedModel(ResourceLocation item, ResourceLocation block) implements IUnbakedGeometry<CreativeUnbakedModel> {
    
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        CreativeBlockModel renderBlock = CreativeCoreClient.BLOCK_MODEL_TYPES.get(block);
        CreativeItemModel renderItem = CreativeCoreClient.ITEM_MODEL_TYPES.get(item);
        if (renderBlock == null && renderItem == null)
            throw new RuntimeException("Could not find renderer " + block + ", " + item);
        if (renderItem != null)
            return renderItem.create(renderBlock);
        return CreativeItemBoxModel.EMPTY.create(renderBlock);
    }
    
}
