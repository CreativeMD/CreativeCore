package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.ValueDisplay;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiSeekBar extends GuiSlider {
    UpdateEvent mouseGrabEvent;
    UpdateEvent mouseDropEvent;
    ValueFunction valueFunction;

    @Deprecated(forRemoval = true)
    public GuiSeekBar(String name, int width, int height, double value, double min, double max) {
        super(name, width, height, value, min, max);
    }

    public GuiSeekBar(String name, double value, double min, double max, @Nullable ValueDisplay display) {
        super(name, value, min, max, display);
        this.valueDisplay = display;
    }

    public GuiSeekBar setMouseGrabEvent(UpdateEvent r) {
        this.mouseGrabEvent = r;
        return this;
    }

    public GuiSeekBar setMouseDropEvent(UpdateEvent r) {
        this.mouseDropEvent = r;
        return this;
    }

    public GuiSeekBar setValueFunction(ValueFunction function) {
        this.valueFunction = function;
        return this;
    }


    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        super.mouseMoved(rect, x, y);
        if (grabbedSlider && mouseGrabEvent != null) mouseGrabEvent.call(this);
    }

    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        super.mouseReleased(rect, x, y, button);
        if (mouseDropEvent != null) mouseDropEvent.call(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        if (valueFunction != null) {
            this.maxValue = valueFunction.getMax();
            this.value = valueFunction.getValue();
        }
        double percent = this.getPercentage();
        int posX = (int) (control.getContentWidth() * percent);
        GuiStyle style = this.getStyle();
        style.get(ControlFormatting.ControlStyleFace.CLICKABLE, false).render(graphics, 0, 0.0, posX, rect.getHeight());

        if (this.textfield != null)
            this.textfield.render(graphics, control, rect, rect, mouseX, mouseY);
        else
            GuiRenderHelper.drawStringCentered(graphics, getDisplayedValue(), (float)rect.getWidth(), (float)rect.getHeight(), -1, true);
    }

    public interface UpdateEvent {
        void call(GuiSeekBar seekBar);
    }

    public interface ValueFunction {
        int getMax();
        int getValue();
    }
}
