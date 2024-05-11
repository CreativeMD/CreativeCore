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
import team.creative.creativecore.client.CreativeCoreClient;

public record CreativeUnbakedModel(ResourceLocation item, ResourceLocation block) implements IModelGeometry<CreativeUnbakedModel> {
    
    @Override
    public BakedModel bake(IModelConfiguration context, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        CreativeBlockModel renderBlock = CreativeCoreClient.BLOCK_MODEL_TYPES.get(block);
        CreativeItemModel renderItem = CreativeCoreClient.ITEM_MODEL_TYPES.get(item);
        if (renderBlock == null && renderItem == null)
            throw new RuntimeException("Could not find renderer " + block + ", " + item);
        if (renderItem != null)
            return renderItem.create(renderBlock);
        return CreativeItemBoxModel.EMPTY.create(renderBlock);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration iModelConfiguration, Function<ResourceLocation, UnbakedModel> function, Set<Pair<String, String>> set) {
        return Collections.EMPTY_LIST;
    }
}
