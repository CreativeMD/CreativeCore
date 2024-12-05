package team.creative.creativecore.client.render.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import team.creative.creativecore.client.CreativeCoreClient;

public record CreativeUnbakedModel(ResourceLocation item, ResourceLocation block) implements UnbakedModel {
    
    @Override
    public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms itemTransforms,
            ContextMap additionalProperties) {
        CreativeBlockModel renderBlock = CreativeCoreClient.BLOCK_MODEL_TYPES.get(block);
        CreativeItemModel renderItem = CreativeCoreClient.ITEM_MODEL_TYPES.get(item);
        if (renderBlock == null && renderItem == null)
            throw new RuntimeException("Could not find renderer " + block + ", " + item);
        if (renderItem != null)
            return renderItem.create(renderBlock);
        return CreativeItemBoxModel.EMPTY.create(renderBlock);
    }
    
    @Override
    public void resolveDependencies(Resolver resolver) {}
    
    @Override
    public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms itemTransforms) {
        throw new UnsupportedOperationException();
    }
    
}
