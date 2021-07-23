package team.creative.creativecore.common.gui.integration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
                mc.setScreen(displayScreen);
                displayScreen = null;
            }
            if (mc.screen instanceof IScaleableGuiScreen) {
                IScaleableGuiScreen gui = (IScaleableGuiScreen) mc.screen;
                
                if (!changed)
                    defaultScale = mc.options.guiScale;
                int maxScale = gui.getMaxScale(mc.getWindow().getWidth(), mc.getWindow().getHeight());
                int scale = Math.min(defaultScale, maxScale);
                if (defaultScale == 0)
                    scale = maxScale;
                if (scale != mc.options.guiScale) {
                    changed = true;
                    mc.options.guiScale = scale;
                    mc.getWindow().setGuiScale(scale);
                    mc.screen.resize(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                }
            } else if (changed) {
                changed = false;
                mc.options.guiScale = defaultScale;
                mc.getWindow().setGuiScale(mc.getWindow().calculateScale(mc.options.guiScale, mc.isEnforceUnicode()));
                if (mc.screen != null)
                    mc.screen.resize(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            }
        }
    }
    
}
