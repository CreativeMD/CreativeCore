package team.creative.creativecore.client.gui.control.simple;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.control.simple.GuiListEntry;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiClientListEntry<T extends GuiListEntry> extends GuiClientLabel<T> {
    
    public GuiClientListEntry(T control) {
        super(control);
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        if (control.selected)
            text.setDefaultColor(realRect.inside(mouseX, mouseY) ? ColorUtils.rgba(230, 230, 0, 255) : ColorUtils.rgba(200, 200, 0, 255));
        else if (realRect.inside(mouseX, mouseY))
            text.setDefaultColor(ColorUtils.YELLOW);
        else
            text.setDefaultColor(ColorUtils.WHITE);
        super.renderContent(graphics, controlRect, realRect, scale, mouseX, mouseY);
        text.setDefaultColor(ColorUtils.WHITE);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        control.consumer.accept(info.button());
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
}
