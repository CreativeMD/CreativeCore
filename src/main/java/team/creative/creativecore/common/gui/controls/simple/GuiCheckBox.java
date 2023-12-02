package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;

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
    
    @OnlyIn(Dist.CLIENT)
    private static StyleDisplay partialStyle;
    public static final int checkBoxWidth = 7;
    
    public boolean value;
    public String title;
    public boolean partial = false;
    
    public GuiCheckBox(String name, String title, boolean value) {
        super(name);
        this.value = value;
        this.title = title;
    }
    
    public GuiCheckBox(String title, boolean value) {
        this(title, title, value);
    }
    
    @Override
    public GuiCheckBox setTranslate(String translate) {
        return (GuiCheckBox) super.setTranslate(translate);
    }

    @Override
    public GuiLabel setScale(float scale) {
        return super.setScale(scale);
    }

    @Override
    public int getPreferredWidth() {
        return super.getPreferredWidth() + checkBoxWidth + 3;
    }
    
    @Override
    public int getPreferredHeight() {
        return Math.max(checkBoxWidth + 3, super.getPreferredHeight());
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        int yoffset = 0;
        
        GuiStyle style = getStyle();
        
        if (!enabled)
            style.disabled.render(matrix, 0, yoffset, checkBoxWidth, checkBoxWidth);
        
        style.get(ControlStyleBorder.SMALL).render(matrix, 0, yoffset, checkBoxWidth, checkBoxWidth);
        style.get(ControlStyleFace.NESTED_BACKGROUND, rect.inside(mouseX, mouseY)).render(matrix, 1, yoffset + 1, checkBoxWidth - 2, checkBoxWidth - 2);
        
        if (value)
            Minecraft.getInstance().font.draw(matrix, "x", 1, yoffset - 1, enabled ? ColorUtils.WHITE : style.fontColorHighlight.toInt());
        else if (partial) {
            if (partialStyle == null)
                partialStyle = new DisplayColor();
            partialStyle.render(matrix, 2, yoffset + 2, checkBoxWidth - 4, checkBoxWidth - 4);
        }
        
        matrix.pushPose();
        matrix.translate(checkBoxWidth + 3, 0, 0);
        text.render(matrix, scale);
        matrix.popPose();
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        this.value = !value;
        raiseEvent(new GuiControlChangedEvent(this));
        return true;
    }
}
