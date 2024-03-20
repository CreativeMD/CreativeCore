package team.creative.creativecore.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;

@Mixin(ArgumentTypeInfos.class)
public interface ArgumentTypeInfosAccessor {
    
    @Accessor("BY_CLASS")
    public static Map<Class<?>, ArgumentTypeInfo<?, ?>> getByClass() {
        throw new AssertionError();
    }
    
}
