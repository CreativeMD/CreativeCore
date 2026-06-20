package team.creative.creativecore.client.gui;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;

public class GuiScreenHandler {
    
    private static int displayWidth;
    private static int displayHeight;
    private static int defaultScale;
    private static boolean changed;
    private static Screen displayScreen;
    
    public static void queueScreen(Screen displayScreen) {
        GuiScreenHandler.displayScreen = displayScreen;
    }
    
    @SubscribeEvent
    public static void onTick(RenderFrameEvent.Pre tick) {
        Minecraft mc = Minecraft.getInstance();
        if (displayScreen != null) {
            mc.gui.setScreen(displayScreen);
            displayScreen = null;
        }
        Window window = mc.getWindow();
        if (window.getWidth() != displayWidth || window.getHeight() != displayHeight) {
            displayWidth = window.getWidth();
            displayHeight = window.getHeight();
            if (mc.gui.screen() instanceof IScaleableGuiScreen) {
                mc.options.guiScale().set(defaultScale);
                window.setGuiScale(window.calculateScale(mc.options.guiScale().get(), mc.isEnforceUnicode()));
                mc.gui.screen().resize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
            }
        }
        
        if (mc.gui.screen() instanceof IScaleableGuiScreen gui) {
            if (!changed)
                defaultScale = mc.options.guiScale().get();
            int maxScale = Math.min(CreativeCore.CONFIG.maxGuiScale, gui.getMaxScale(window.getWidth(), window.getHeight()));
            int scale = Math.min(defaultScale, maxScale);
            if (defaultScale == 0)
                scale = maxScale;
            if (scale != mc.options.guiScale().get()) {
                changed = true;
                mc.options.guiScale().set(scale);
                window.setGuiScale(scale);
                mc.gui.screen().resize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
            }
        } else if (changed) {
            changed = false;
            mc.options.guiScale().set(defaultScale);
            window.setGuiScale(window.calculateScale(mc.options.guiScale().get(), mc.isEnforceUnicode()));
            if (mc.gui.screen() != null)
                mc.gui.screen().resize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
        }
    }
    
}
