package team.creative.creativecore.client.gui.control.simple;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.control.simple.GuiButton.GuiButtonDist;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientButton<T extends GuiButton> extends GuiClientLabel<T> implements GuiButtonDist {
    
    protected Consumer<Integer> pressed;
    protected boolean hoverEffect;
    
    public GuiClientButton(T control) {
        super(control);
    }
    
    @Override
    public void setHoverEffect(boolean active) {
        this.hoverEffect = active;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        if (hoverEffect)
            if (rect.inside(mouseX, mouseY))
                text.setDefaultColor(getStyle().fontColorHighlight.toInt());
            else
                text.setDefaultColor(getStyle().fontColor.toInt());
        super.renderContent(graphics, mouseX, mouseY);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        if (pressed != null)
            pressed.accept(info.button());
        return true;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    @Override
    public void setPressed(Consumer<Integer> pressed) {
        this.pressed = pressed;
    }
    
}
