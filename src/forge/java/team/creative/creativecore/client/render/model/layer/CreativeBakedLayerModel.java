package team.creative.creativecore.client.render.model.layer;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CreativeBakedLayerModel implements IDynamicBakedModel {
    
    private final boolean isAmbientOcclusion;
    private final boolean isGui3d;
    private final boolean isSideLit;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;
    private final ItemTransforms transforms;
    public final ImmutableList<CreativeBakedItemLayer> layers;
    
    public CreativeBakedLayerModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, ImmutableList<CreativeBakedItemLayer> layers) {
        this.isAmbientOcclusion = isAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.isSideLit = isSideLit;
        this.particle = particle;
        this.overrides = overrides;
        this.transforms = transforms;
        this.layers = layers;
    }
    
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public boolean useAmbientOcclusion() {
        return isAmbientOcclusion;
    }
    
    @Override
    public boolean isGui3d() {
        return isGui3d;
    }
    
    @Override
    public boolean usesBlockLight() {
        return isSideLit;
    }
    
    @Override
    public boolean isCustomRenderer() {
        return true;
    }
    
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }
    
    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }
    
    @Override
    public ItemTransforms getTransforms() {
        return transforms;
    }
    
    public void render(ItemRenderer renderer, ItemStack stack, ItemDisplayContext context, boolean left, PoseStack pose, MultiBufferSource buffer, int packedLight,
            int packedOverlay) {
        for (CreativeBakedItemLayer layer : layers)
            layer.resolve(stack).render(renderer, stack, context, left, pose, buffer, packedLight, packedOverlay);
    }
    
}
