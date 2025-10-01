package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.CreativeFabricLoader;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    
    @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("HEAD"), require = 1)
    public void render(DeltaTracker delta, boolean p_109096_, CallbackInfo info) {
        for (Runnable run : ((CreativeFabricLoader) CreativeCore.loader()).RENDER_START)
            run.run();
    }
    
}
