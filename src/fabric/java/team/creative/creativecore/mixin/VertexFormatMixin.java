package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.vertex.VertexFormat;

import it.unimi.dsi.fastutil.ints.IntList;
import team.creative.creativecore.client.VertexFormatExtender;

@Mixin(VertexFormat.class)
public class VertexFormatMixin implements VertexFormatExtender {
    
    @Shadow
    @Final
    private IntList offsets;
    
    @Override
    public int getOffset(int index) {
        return offsets.getInt(index);
    }
    
}
