package team.creative.creativecore.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class MouseHandlerMixin extends Screen {
    protected MouseHandlerMixin(Component title) {
        super(title);
    }

    @Inject(method = "mouseReleased", at = @At(value = "HEAD"))
    private void handleWhenYouReleaseMouse(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        // PORTED FIX FROM FORGE: this was required to make sliders work properly
        super.mouseReleased(mouseX, mouseY, button);
    }
}
