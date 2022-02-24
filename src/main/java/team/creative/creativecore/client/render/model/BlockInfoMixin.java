package team.creative.creativecore.client.render.model;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraftforge.client.model.pipeline.BlockInfo;

@Mixin(BlockInfo.class)
public abstract class BlockInfoMixin implements BlockInfoExtension {
    
    @Unique
    public int customTint;
    
    @Inject(at = @At(value = "HEAD"), method = "getColorMultiplier(I)I", cancellable = true)
    public void getColorMultiplierHook(int tint, CallbackInfoReturnable<Integer> info) {
        if (customTint != -1)
            info.setReturnValue(customTint);
    }
    
    @Override
    public void setCustomTint(int tint) {
        this.customTint = tint;
    }
}
