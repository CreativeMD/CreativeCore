package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;

import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils.ColorPart;
import team.creative.creativecore.common.util.type.Color;

public class GuiColoredSteppedSlider extends GuiSteppedSlider {
    
    public GuiColorPicker picker;
    public ColorPart part;
    
    public GuiColoredSteppedSlider(String name, GuiColorPicker picker, ColorPart part) {
        super(name, part.get(picker.color), 0, 255);
        this.picker = picker;
        this.part = part;
    }
    
    @Override
    public void setValue(double value) {
        super.setValue((int) value);
        if (part != null) {
            part.set(picker.color, (int) this.value);
            picker.onColorChanged();
        }
    }
    
    @Override
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        if (part == ColorPart.ALPHA) {
            Color startColor = new Color(picker.color);
            startColor.setAlpha(0);
            Color endColor = new Color(picker.color);
            endColor.setAlpha(255);
            GuiRenderHelper.horizontalGradientRect(pose, 0, 0, (int) rect.getWidth(), (int) rect.getHeight(), startColor.toInt(), endColor.toInt());
        } else
            GuiRenderHelper.horizontalGradientMaskRect(pose, 0, 0, (int) rect.getWidth(), (int) rect.getHeight(), picker.color.toInt(), part.code);
        super.renderContent(pose, control, rect, mouseX, mouseY);
    }
}