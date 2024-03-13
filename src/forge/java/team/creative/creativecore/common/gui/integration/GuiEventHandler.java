package team.creative.creativecore.common.gui.integration;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    
    @OnlyIn(value = Dist.CLIENT)
    @SubscribeEvent
    public static void onTick(RenderTickEvent tick) {
        Minecraft mc = Minecraft.getInstance();
        if (tick.phase == Phase.START) {
            if (displayScreen != null) {
                mc.setScreen(displayScreen);
                displayScreen = null;
            }
            Window window = mc.getWindow();
            if (window.getWidth() != displayWidth || window.getHeight() != displayHeight) {
                displayWidth = window.getWidth();
                displayHeight = window.getHeight();
                if (mc.screen instanceof IScaleableGuiScreen gui) {
                    mc.options.guiScale = defaultScale;
                    window.setGuiScale(window.calculateScale(mc.options.guiScale, mc.isEnforceUnicode()));
                    if (mc.screen != null)
                        mc.screen.resize(mc, window.getGuiScaledWidth(), window.getGuiScaledHeight());
                }
            }

            if (mc.screen instanceof IScaleableGuiScreen gui) {
                if (!changed)
                    defaultScale = mc.options.guiScale;
                int maxScale = gui.getMaxScale(window.getWidth(), window.getHeight());
                int scale = Math.min(defaultScale, maxScale);
                if (defaultScale == 0)
                    scale = maxScale;
                if (scale != mc.options.guiScale) {
                    changed = true;
                    mc.options.guiScale = scale;
                    window.setGuiScale(scale);
                    mc.screen.resize(mc, window.getGuiScaledWidth(), window.getGuiScaledHeight());
                }
            } else if (changed) {
                changed = false;
                mc.options.guiScale = defaultScale;
                window.setGuiScale(window.calculateScale(mc.options.guiScale, mc.isEnforceUnicode()));
                if (mc.screen != null)
                    mc.screen.resize(mc, window.getGuiScaledWidth(), window.getGuiScaledHeight());
            }
        }
    }
    
}
