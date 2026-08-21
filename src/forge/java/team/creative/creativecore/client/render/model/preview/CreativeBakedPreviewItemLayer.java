package team.creative.creativecore.client.render.model.preview;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.render.model.layer.CreativeBakedItemLayer;

public record CreativeBakedPreviewItemLayer(ItemModelPreview model) implements CreativeBakedItemLayer {
    
    @Override
    public void render(ItemRenderer renderer, ItemStack stack, ItemDisplayContext context, boolean left, PoseStack pose, MultiBufferSource buffer, int packedLight,
            int packedOverlay) {
        
        if (context != ItemDisplayContext.GUI)
            return;
        
        var topStack = model.getPreview(stack);
        if (stack.isEmpty())
            return;
        
        pose.pushPose();
        
        pose.translate(0.2, -0.18, 1);
        pose.scale(0.65F, 0.65F, 0.65F);
        
        renderer.render(topStack, context, left, pose, buffer, packedLight, packedOverlay, renderer.getModel(topStack, null, null, 0));
        
        pose.popPose();
    }
    
}
