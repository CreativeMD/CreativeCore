package team.creative.creativecore.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public interface BufferBuilderAccessor {
    
    @Accessor
    public int getVertices();
    
    @Accessor
    public int getNextElementByte();
    
    @Accessor
    public ByteBuffer getBuffer();
    
}
