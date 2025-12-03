package team.creative.creativecore.client.gui.control.simple;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.control.simple.GuiColorPicker;
import team.creative.creativecore.common.gui.control.simple.GuiColoredSteppedSlider;
import team.creative.creativecore.common.gui.control.simple.GuiColoredSteppedSlider.GuiColoredSteppedSliderDist;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils.ColorPart;
import team.creative.creativecore.common.util.type.Color;

public class GuiClientColoredSteppedSlider<T extends GuiColoredSteppedSlider> extends GuiClientSteppedSlider<T> implements GuiColoredSteppedSliderDist {
    
    public GuiColorPicker picker;
    public ColorPart part;
    
    public GuiClientColoredSteppedSlider(T control) {
        super(control);
    }
    
    @Override
    public void setPicker(GuiColorPicker picker) {
        this.picker = picker;
    }
    
    @Override
    public void setPart(ColorPart part) {
        this.part = part;
    }
    
    @Override
    public void setValue(double value, boolean notify) {
        super.setValue((int) value, notify);
        if (part != null) {
            part.set(picker.color, this.getIntValue());
            picker.onColorChanged(true);
        }
    }
    
    @Override
    public void setValueSilent(int value) {
        this.value = Mth.clamp(value, (int) minValue, (int) maxValue);
        if (part != null)
            part.set(picker.color, this.getIntValue());
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        if (part == ColorPart.ALPHA) {
            Color startColor = new Color(picker.color);
            startColor.setAlpha(0);
            Color endColor = new Color(picker.color);
            endColor.setAlpha(255);
            ((CreativeGuiGraphics) graphics).horizontalGradientRect(0, 0, rect.getContentWidth(), rect.getContentHeight(), startColor.toInt(), endColor.toInt());
        } else
            ((CreativeGuiGraphics) graphics).horizontalGradientMaskRect(0, 0, rect.getContentWidth(), rect.getContentHeight(), picker.color.toInt(), part.code);
        super.renderContent(graphics, controlRect, realRect, scale, mouseX, mouseY);
    }
    
}
