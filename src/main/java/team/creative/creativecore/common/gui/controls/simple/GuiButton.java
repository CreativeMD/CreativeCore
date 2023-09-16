package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiButton extends GuiLabel {
    
    protected Consumer<Integer> pressed;
    
    public GuiButton(String name, Consumer<Integer> pressed) {
        this(name, 1.0f, pressed);
    }

    public GuiButton(String name, float scale, Consumer<Integer> pressed) {
        super(name, scale);
        this.pressed = pressed;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        if (pressed != null)
            pressed.accept(button);
        return true;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }

    @Override
    protected int minWidth(int availableWidth) {
        return 0;
    }

    @Override
    protected int preferredWidth(int availableWidth) {
        return text.getTotalWidth();
    }

    @Override
    protected int minHeight(int width, int availableHeight) {
        return Minecraft.getInstance().font.lineHeight;
    }

    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return text.getTotalHeight();
    }
}