package team.creative.creativecore.common.gui.integration;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;

public class GuiEventHandler {
    
    private static int displayWidth;
    private static int displayHeight;
    private static int defaultScale;
    private static boolean changed;
    private static Screen displayScreen;
    
    public static void queueScreen(Screen displayScreen) {
        GuiEventHandler.displayScreen = displayScreen;
    }
    
    public static void onTick(Minecraft mc) {
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
            int maxScale = gui.getMaxScale(window.getWidth(), window.getHeight());
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
            mc.getWindow().setGuiScale(mc.getWindow().calculateScale(mc.options.guiScale().get(), mc.isEnforceUnicode()));
            if (mc.gui.screen() != null)
                mc.gui.screen().resize(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        }
    }
    
}
