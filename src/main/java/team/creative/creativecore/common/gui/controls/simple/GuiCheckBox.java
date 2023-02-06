package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiCheckBox extends GuiLabel {
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    private static StyleDisplay PARTIAL_STYLE;
    public static final int CHECKBOX_WIDTH = 7;
    
    public boolean value = false;
    public String title;
    public boolean partial = false;
    public Consumer<Boolean> changed;
    
    public GuiCheckBox(String name, boolean value) {
        super(name);
        this.value = value;
    }
    
    public GuiCheckBox consumeChanged(Consumer<Boolean> changed) {
        this.changed = changed;
        return this;
    }
    
    @Override
    public GuiCheckBox setTranslate(String translate) {
        return (GuiCheckBox) super.setTranslate(translate);
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
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        int yoffset = 0;
        
        GuiStyle style = getStyle();
        
        if (!enabled)
            style.disabled.render(matrix, 0, yoffset, CHECKBOX_WIDTH, CHECKBOX_WIDTH);
        
        style.get(ControlStyleBorder.SMALL).render(matrix, 0, yoffset, CHECKBOX_WIDTH, CHECKBOX_WIDTH);
        style.get(ControlStyleFace.NESTED_BACKGROUND, rect.inside(mouseX, mouseY)).render(matrix, 1, yoffset + 1, CHECKBOX_WIDTH - 2, CHECKBOX_WIDTH - 2);
        
        if (value)
            Minecraft.getInstance().font.draw(matrix, "x", 1, yoffset - 1, enabled ? ColorUtils.WHITE : style.fontColorHighlight.toInt());
        else if (partial) {
            if (PARTIAL_STYLE == null)
                PARTIAL_STYLE = new DisplayColor();
            PARTIAL_STYLE.render(matrix, 2, yoffset + 2, CHECKBOX_WIDTH - 4, CHECKBOX_WIDTH - 4);
        }
        
        matrix.pushPose();
        matrix.translate(CHECKBOX_WIDTH + 3, 0, 0);
        text.render(matrix);
        matrix.popPose();
    }
    
    public void set(boolean value) {
        if (this.value == value)
            return;
        this.value = value;
        raiseEvent(new GuiControlChangedEvent(this));
        if (changed != null)
            changed.accept(value);
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        set(!value);
        return true;
    }
    
}
