package team.creative.creativecore.mixin;

import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import org.spongepowered.asm.mixin.Mixin;
import team.creative.creativecore.client.render.model.CreativeQuadLighter;

@Mixin(value = VertexLighterFlat.class, remap = false)
public abstract class QuadLighterMixin implements CreativeQuadLighter {
//
//    @Unique
//    public int customTint = -1;
//
//    @Shadow
//    public int tint;
//
//    @Shadow
//    @Final
//    public float[] cachedTintColor;
//
//    @Override
//    @Accessor
//    public abstract void setState(BlockState state);
//
//    @Inject(at = @At(value = "HEAD"), method = "getColorFast(I)[F", cancellable = true, remap = false)
//    public void getColorMultiplierHook(int tint, CallbackInfoReturnable info) {
//        if (customTint != -1) {
//            this.tint = tint;
//            cachedTintColor[0] = ((customTint >> 16) & 0xFF) / 255F;
//            cachedTintColor[1] = ((customTint >> 8) & 0xFF) / 255F;
//            cachedTintColor[2] = (customTint & 0xFF) / 255F;
//            info.setReturnValue(cachedTintColor);
//        }
//    }
//
//    @Override
//    public void setCustomTint(int tint) {
//        this.customTint = tint;
//    }
}
