package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

public class GuiButtonIcon extends GuiIcon {
    protected Consumer<Integer> pressed;
    private ControlFormatting formatting;

    public GuiButtonIcon(String name, Icon icon, Consumer<Integer> pressed) {
        super(name, icon);
        this.pressed = pressed;
        this.formatting = ControlFormatting.CLICKABLE;
        this.color = Color.WHITE;
        this.shadow = Color.BLACK;
    }

    public GuiButtonIcon setControlFormatting(ControlFormatting formatting) {
        this.formatting = formatting;
        return this;
    }

    @Override
    public ControlFormatting getControlFormatting() {
        return formatting;
    }

    @Override
    public StyleDisplay getBackground(GuiStyle style, StyleDisplay display) {
        return display;
    }

    @Override
    public GuiButtonIcon setIcon(Icon icon) {
        super.setIcon(icon);
        return this;
    }

    @Override
    public GuiButtonIcon setColor(Color color) {
        super.setColor(color);
        return this;
    }

    @Override
    public GuiButtonIcon setShadow(Color colorShadow) {
        super.setShadow(colorShadow);
        return this;
    }

    @Override
    public GuiButtonIcon setSquared(boolean squared) {
        super.setSquared(squared);
        return this;
    }

    @Override
    protected int preferredWidth(int availableWidth) {
        return 20;
    }

    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 20;
    }

    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        if (pressed != null)
            pressed.accept(button);
        return true;
    }
}
