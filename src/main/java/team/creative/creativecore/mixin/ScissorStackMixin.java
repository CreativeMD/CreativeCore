package team.creative.creativecore.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import team.creative.creativecore.client.render.ScissorStackExtender;

@Mixin(targets = "net.minecraft.client.gui.GuiGraphics$ScissorStack")
public abstract class ScissorStackMixin implements ScissorStackExtender {
    
    @Unique
    private boolean override;
    
    @Unique
    private ScreenRectangle overrideRect;
    
    @WrapMethod(method = "peek()Lnet/minecraft/client/gui/navigation/ScreenRectangle;", require = 1)
    public ScreenRectangle peek(Operation<ScreenRectangle> original) {
        if (override)
            return overrideRect;
        return original.call();
    }
    
    @Override
    public void setOverrideScissor(@Nullable ScreenRectangle rect) {
        this.override = true;
        this.overrideRect = rect;
    }
    
    @Override
    public void clearOverrideScissor() {
        this.override = false;
        this.overrideRect = null;
    }
    
}
