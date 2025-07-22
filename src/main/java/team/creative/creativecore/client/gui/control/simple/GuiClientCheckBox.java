package team.creative.creativecore.client.gui.control.simple;

import java.util.function.Consumer;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.control.simple.GuiCheckBox;
import team.creative.creativecore.common.gui.control.simple.GuiCheckBox.GuiCheckBoxDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiClientCheckBox<T extends GuiCheckBox> extends GuiClientLabel<T> implements GuiCheckBoxDist {
    
    public static final int CHECKBOX_WIDTH = 7;
    public static final StyleDisplay PARTIAL_STYLE = new DisplayColor();
    
    public boolean value;
    public boolean partial = false;
    public Consumer<Boolean> changed;
    
    public GuiClientCheckBox(T control) {
        super(control);
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return super.preferredWidth(availableWidth) + CHECKBOX_WIDTH + 3;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return Math.max(CHECKBOX_WIDTH + 3, super.preferredHeight(width, availableHeight));
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        int yoffset = 0;
        
        Matrix3x2fStack pose = graphics.pose();
        GuiStyle style = getStyle();
        
        if (!enabled && getControlFormatting().hasDisabledEffect)
            style.disabled.render(graphics, 0, yoffset, CHECKBOX_WIDTH, CHECKBOX_WIDTH);
        
        style.get(ControlStyleBorder.SMALL).render(graphics, 0, yoffset, CHECKBOX_WIDTH, CHECKBOX_WIDTH);
        style.get(ControlStyleFace.NESTED_BACKGROUND, rect.inside(mouseX, mouseY)).render(graphics, 1, yoffset + 1, CHECKBOX_WIDTH - 2, CHECKBOX_WIDTH - 2);
        
        if (value)
            graphics.drawString(Minecraft.getInstance().font, "x", 1, yoffset - 1, enabled ? ColorUtils.WHITE : style.fontColorHighlight.toInt(), false);
        else if (partial)
            PARTIAL_STYLE.render(graphics, 2, yoffset + 2, CHECKBOX_WIDTH - 4, CHECKBOX_WIDTH - 4);
        
        pose.pushMatrix();
        pose.translate(CHECKBOX_WIDTH + 3, 0);
        text.render(graphics);
        pose.popMatrix();
    }
    
    @Override
    public void consumeChanged(Consumer<Boolean> changed) {
        this.changed = changed;
    }
    
    @Override
    public void set(boolean value) {
        if (this.value == value)
            return;
        this.value = value;
        raiseEvent(new GuiControlChangedEvent(control));
        if (changed != null)
            changed.accept(value);
    }
    
    @Override
    public boolean get() {
        return value;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        set(!value);
        return true;
    }
    
}
