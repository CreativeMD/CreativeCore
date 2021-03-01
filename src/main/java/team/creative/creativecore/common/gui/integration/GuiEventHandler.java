package team.creative.creativecore.common.gui.integration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import team.creative.creativecore.common.gui.IScaleableGuiScreen;

public class GuiEventHandler {
    
    @OnlyIn(value = Dist.CLIENT)
    public static int defaultScale;
    @OnlyIn(value = Dist.CLIENT)
    public static boolean changed;
    
    @OnlyIn(value = Dist.CLIENT)
    private static Screen displayScreen;
    
    @OnlyIn(value = Dist.CLIENT)
    public static void queueScreen(Screen displayScreen) {
        GuiEventHandler.displayScreen = displayScreen;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    @SubscribeEvent
    public static void onTick(RenderTickEvent tick) {
        Minecraft mc = Minecraft.getInstance();
        if (tick.phase == Phase.START) {
            if (displayScreen != null) {
                mc.displayGuiScreen(displayScreen);
                displayScreen = null;
            }
            if (mc.currentScreen instanceof IScaleableGuiScreen) {
                IScaleableGuiScreen gui = (IScaleableGuiScreen) mc.currentScreen;
                
                if (!changed)
                    defaultScale = mc.gameSettings.guiScale;
                int maxScale = gui.getMaxScale(mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight());
                int scale = Math.min(defaultScale, maxScale);
                if (defaultScale == 0)
                    scale = maxScale;
                if (scale != mc.gameSettings.guiScale) {
                    changed = true;
                    mc.gameSettings.guiScale = scale;
                    mc.getMainWindow().setGuiScale(scale);
                    mc.currentScreen.resize(mc, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
                }
            } else if (changed) {
                changed = false;
                mc.gameSettings.guiScale = defaultScale;
                mc.getMainWindow().setGuiScale(mc.getMainWindow().calcGuiScale(mc.gameSettings.guiScale, mc.getForceUnicodeFont()));
                if (mc.currentScreen != null)
                    mc.currentScreen.resize(mc, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
            }
        }
    }
    
}
