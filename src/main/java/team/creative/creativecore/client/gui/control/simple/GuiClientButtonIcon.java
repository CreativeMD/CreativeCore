package team.creative.creativecore.client.gui.control.simple;

import java.util.function.Consumer;

import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.control.simple.GuiButtonIcon.GuiButtonIconDist;
import team.creative.creativecore.common.gui.control.simple.GuiIcon;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;

public class GuiClientButtonIcon<T extends GuiIcon> extends GuiClientIcon<T> implements GuiButtonIconDist {
    
    protected Consumer<Integer> pressed;
    
    public GuiClientButtonIcon(T control) {
        super(control);
    }
    
    @Override
    public StyleDisplay getBackground(GuiStyle style, StyleDisplay display) {
        return display;
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
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        if (pressed != null)
            pressed.accept(info.button());
        return true;
    }
    
    @Override
    public void setPressed(Consumer<Integer> pressed) {
        this.pressed = pressed;
    }
    
}
