package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;

@Mixin(value = LightPipelineAwareModelBlockRenderer.class, remap = false)
public interface ForgeModelBlockRendererAccessor {
    
    @Accessor
    public ThreadLocal<QuadLighter> getFlatLighter();
    
    @Accessor
    public ThreadLocal<QuadLighter> getSmoothLighter();
    
}
