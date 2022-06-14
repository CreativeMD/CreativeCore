package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.StringSplitter;

@Mixin(StringSplitter.class)
public interface StringSplitterAccessor {
    @Accessor
    StringSplitter.WidthProvider getWidthProvider();
}
