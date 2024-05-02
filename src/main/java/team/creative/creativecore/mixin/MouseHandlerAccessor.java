package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {
    @Accessor
    double getLastHandleMovementTime();
}
