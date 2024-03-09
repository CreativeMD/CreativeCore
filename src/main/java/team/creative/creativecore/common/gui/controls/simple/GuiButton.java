package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

import java.util.function.Consumer;

public class GuiButton extends GuiLabel {
    
    protected Consumer<Integer> pressed;
    
    public GuiButton(String name, Consumer<Integer> pressed) {
        super(name);
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
