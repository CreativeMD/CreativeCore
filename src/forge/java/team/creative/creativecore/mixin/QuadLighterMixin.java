package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;
import team.creative.creativecore.client.render.model.CreativeQuadLighter;

@Mixin(value = QuadLighter.class, remap = false)
public abstract class QuadLighterMixin implements CreativeQuadLighter {
    
    @Unique
    public int customTint = -1;
    
    @Shadow
    public int cachedTintIndex;
    
    @Shadow
    @Final
    public float[] cachedTintColor;
    
    @Override
    @Accessor
    public abstract void setState(BlockState state);
    
    @Inject(at = @At(value = "HEAD"), method = "getColorFast(I)[F", cancellable = true, remap = false)
    public void getColorMultiplierHook(int tint, CallbackInfoReturnable info) {
        if (customTint != -1) {
            cachedTintIndex = tint;
            cachedTintColor[0] = ((customTint >> 16) & 0xFF) / 255F;
            cachedTintColor[1] = ((customTint >> 8) & 0xFF) / 255F;
            cachedTintColor[2] = (customTint & 0xFF) / 255F;
            info.setReturnValue(cachedTintColor);
        }
    }
    
    @Override
    public void setCustomTint(int tint) {
        this.customTint = tint;
    }
}
