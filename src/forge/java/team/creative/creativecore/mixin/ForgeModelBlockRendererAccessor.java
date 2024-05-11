package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;

@Mixin(value = ForgeBlockModelRenderer.class, remap = false)
public interface ForgeModelBlockRendererAccessor {
    
    @Accessor("lighterFlat")
    public ThreadLocal<VertexLighterFlat> getFlatLighter();
    
    @Accessor("lighterSmooth")
    public ThreadLocal<VertexLighterFlat> getSmoothLighter();
    
}
