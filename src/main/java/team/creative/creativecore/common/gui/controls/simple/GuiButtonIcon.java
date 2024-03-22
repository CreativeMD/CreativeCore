package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

import java.util.function.Consumer;

public class GuiButtonIcon extends GuiIcon {
    protected boolean fixed;
    protected Consumer<Integer> pressed;
    private ControlFormatting formatting;

    public GuiButtonIcon(String name, Icon icon, boolean iconSquared, Consumer<Integer> pressed) {
        super(name, icon, iconSquared);
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
    public GuiButtonIcon setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public GuiButtonIcon setColor(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public GuiButtonIcon setShadow(Color colorShadow) {
        this.shadow = colorShadow;
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
