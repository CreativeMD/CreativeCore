package team.creative.creativecore.client.gui.control.simple;

import net.minecraft.client.gui.GuiGraphics;
import team.creative.creativecore.common.gui.control.simple.GuiLabelHover;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiClientLabelHover extends GuiClientLabel<GuiLabelHover> {
    
    public int darken = ColorUtils.rgb(0, 0, 40);
    
    public GuiClientLabelHover(GuiLabelHover control) {
        super(control);
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        int color = text.getDefaultColor();
        if (realRect.inside(mouseX, mouseY))
            text.setDefaultColor(ColorUtils.substract(color, darken));
        text.render(graphics);
        text.setDefaultColor(color);
    }
    
}
