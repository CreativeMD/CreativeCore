package team.creative.creativecore.client.render.model.layer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface CreativeBakedItemLayer {
    
    @Nullable
    public default CreativeBakedItemLayer resolve(ItemStack stack) {
        return this;
    }
    
    public void render(ItemRenderer renderer, ItemStack stack, ItemDisplayContext context, boolean left, PoseStack pose, MultiBufferSource buffer, int packedLight,
            int packedOverlay);
    
    public static class CreativeBakedItemLayerWrapper implements CreativeBakedItemLayer {
        
        public final BakedModel model;
        
        public CreativeBakedItemLayerWrapper(BakedModel model) {
            this.model = model;
        }
        
        @Override
        public void render(ItemRenderer renderer, ItemStack stack, ItemDisplayContext context, boolean left, PoseStack pose, MultiBufferSource buffer, int packedLight,
                int packedOverlay) {
            boolean flat = context == ItemDisplayContext.GUI && !model.usesBlockLight();
            if (flat)
                Lighting.setupForFlatItems();
            renderer.render(stack, context, left, pose, buffer, packedLight, packedOverlay, model);
            if (buffer instanceof MultiBufferSource.BufferSource s)
                s.endBatch();
            if (flat)
                Lighting.setupFor3DItems();
            
        }
        
    }
    
}
