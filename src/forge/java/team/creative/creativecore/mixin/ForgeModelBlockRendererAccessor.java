package team.creative.creativecore.mixin;

import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ForgeBlockModelRenderer.class, remap = false)
public interface ForgeModelBlockRendererAccessor {
    
    @Accessor("lighterFlat")
    public ThreadLocal<VertexLighterFlat> getFlatLighter();
    
    @Accessor("lighterSmooth")
    public ThreadLocal<VertexLighterFlat> getSmoothLighter();
    
}
