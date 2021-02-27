package team.creative.creativecore.common.gui.controls;

import java.util.function.Consumer;

import net.minecraft.util.SoundEvents;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiButton extends GuiLabel {
    
    protected Consumer<Integer> pressed;
    
    public GuiButton(String name, int x, int y, Consumer<Integer> pressed) {
        super(name, x, y);
        this.pressed = pressed;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
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
