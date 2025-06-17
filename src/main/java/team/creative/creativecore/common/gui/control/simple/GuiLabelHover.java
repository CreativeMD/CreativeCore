package team.creative.creativecore.common.gui.control.simple;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiLabelHover extends GuiLabel {
    
    public int darken = ColorUtils.rgb(0, 0, 40);
    
    public GuiLabelHover(String name) {
        super(name);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        int color = text.getDefaultColor();
        if (realRect.inside(mouseX, mouseY))
            text.setDefaultColor(ColorUtils.substract(color, darken));
        text.render(graphics);
        text.setDefaultColor(color);
    }
    
}
