package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;
import team.creative.creativecore.common.util.text.content.ContentItemStack;

@Mixin(ComponentSerialization.class)
public class ComponentSerializationMixin {
    
    @Inject(at = @At("TAIL"), method = "bootstrap")
    private static void register(ExtraCodecs.LateBoundIdMapper<String, MapCodec<? extends ComponentContents>> lateBoundIdMapper, CallbackInfo info) {
        lateBoundIdMapper.put("stack", ContentItemStack.CODEC);
    }
}
