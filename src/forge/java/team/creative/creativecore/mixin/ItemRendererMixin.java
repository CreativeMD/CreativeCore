package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.render.model.layer.CreativeBakedLayerModel;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    
    @Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At("HEAD"), cancellable = true)
    public void renderByItem(ItemStack stack, ItemDisplayContext context, boolean left, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay,
            BakedModel model, CallbackInfo info) {
        if (model instanceof CreativeBakedLayerModel l) {
            l.render((ItemRenderer) (Object) this, stack, context, left, pose, buffer, packedLight, packedOverlay);
            info.cancel();
        }
    }
    
}
