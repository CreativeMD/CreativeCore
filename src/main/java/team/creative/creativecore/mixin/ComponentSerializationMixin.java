package team.creative.creativecore.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.MapCodec;

import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;
import team.creative.creativecore.common.util.text.content.ContentItemStack;

@Mixin(ComponentSerialization.class)
public class ComponentSerializationMixin {
    
    @WrapOperation(method = "createCodec(Lcom/mojang/serialization/Codec;)Lcom/mojang/serialization/Codec;", require = 1, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/chat/ComponentSerialization;createLegacyComponentMatcher([Lnet/minecraft/util/StringRepresentable;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"))
    private static <T extends StringRepresentable, E> MapCodec<E> createLegacyComponentMatcherWrapper(T[] types, Function<T, MapCodec<? extends E>> typeToCodec, Function<E, T> contentToType, String fieldName, Operation<MapCodec<E>> operation) {
        T[] newTypes = (T[]) new StringRepresentable[types.length + 1];
        for (int i = 0; i < types.length; i++)
            newTypes[i] = types[i];
        newTypes[types.length] = (T) ContentItemStack.TYPE;
        return operation.call(newTypes, typeToCodec, contentToType, fieldName);
    }
}
