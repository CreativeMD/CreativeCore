package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.ValueFormatter;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiSeekBar extends GuiSlider {
    protected ValueSuppliers valueSuppliers;

    @Deprecated
    public GuiSeekBar(String name, double value, double min, double max) {
        super(name, value, min, max);
    }

    public GuiSeekBar(String name, double value, double min, double max, ValueFormatter formatter) {
        super(name, value, min, max, formatter);
    }

    public GuiSeekBar setValueFunction(ValueSuppliers function) {
        this.valueSuppliers = function;
        return this;
    }

    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        super.mouseMoved(rect, x, y);
    }

    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        super.mouseReleased(rect, x, y, button);
    }

    @Override
    public void render(GuiGraphics graphics, GuiChildControl control, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        if (valueSuppliers != null) {
            this.maxValue = valueSuppliers.getMax();
            this.value = valueSuppliers.getValue();
        }
        double percent = this.getPercentage();
        int posX = (int) (control.getContentWidth() * percent);
        GuiStyle style = this.getStyle();
        style.get(ControlFormatting.ControlStyleFace.CLICKABLE, false).render(pose, 0, 0.0, posX, realRect.getHeight());

        if (this.textfield != null)
            this.textfield.render(graphics, control, controlRect, realRect, 1.0f, mouseX, mouseY);
        else
            GuiRenderHelper.drawStringCentered(pose, getTextByValue(), (float)realRect.getWidth(), (float)realRect.getHeight(), -1, true);
    }

    public interface ValueSuppliers {
        int getMax();
        int getValue();
    }
}