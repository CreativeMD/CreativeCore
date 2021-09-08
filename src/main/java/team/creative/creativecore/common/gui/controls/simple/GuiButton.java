package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiButton extends GuiLabel {
    
    protected Consumer<Integer> pressed;
    
    public GuiButton(String name, Consumer<Integer> pressed) {
        super(name);
        this.pressed = pressed;
    }
    
    public GuiButton(String name, int width, int height, Consumer<Integer> pressed) {
        super(name, width, height);
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
    
}
