package team.creative.creativecore.common.gui.controls.simple;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.*;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiSlider extends GuiControl implements IGuiParent {
    
    public double maxValue;
    public double minValue;
    public double value;
    private final ValueFormatter formatter;
    protected boolean grabbedSlider;
    public int sliderWidth = 4;
    
    protected GuiTextfield textfield;

    @Deprecated
    public GuiSlider(String name, double value, double min, double max) {
        this(name, value, min, max, ValueFormatter.NONE);
    }

    public GuiSlider(String name, double value, double min, double max, ValueFormatter formatter) {
        super(name);
        this.minValue = min;
        this.maxValue = max;
        this.formatter = formatter;
        setValue(value);
    }
    
    public String getTextByValue() {
        return formatter.get(value, maxValue);
    }
    
    public String getTextfieldValue() {
        return getTextByValue();
    }
    
    public double getPercentage() {
        return (this.value - this.minValue) / (this.maxValue - this.minValue);
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (button == 0) {
            if (textfield != null)
                return textfield.mouseClicked(rect, x, y, button);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            grabbedSlider = true;
            mouseMoved(rect, x, y);
            return true;
        } else if (button == 1) {
            grabbedSlider = false;
            textfield = createTextfield(rect);
            textfield.focus();
            textfield.setText(getTextfieldValue());
            textfield.setCursorPositionEnd();
            textfield.setParent(this);
            int width = (int) rect.getWidth();
            textfield.flowX(width, width);
            return true;
        }
        return false;
    }
    
    protected GuiTextfield createTextfield(Rect rect) {
        return new GuiTextfield(getNestedName() + ".text").setFloatOnly().setDim((int) rect.getWidth() - getContentOffset() * 2, (int) rect.getHeight() - getContentOffset() * 2);
    }
    
    public void closeTextField() {
        double value = this.value;
        try {
            setValue(textfield.parseFloat());
            playSound(SoundEvents.UI_BUTTON_CLICK);
        } catch (NumberFormatException e) {
            setValue(value);
        }
        textfield = null;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (textfield != null) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                closeTextField();
                return true;
            }
            return textfield.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (textfield != null)
            return textfield.charTyped(codePoint, modifiers);
        return super.charTyped(codePoint, modifiers);
    }
    
    public void setValue(double value) {
        this.value = Math.max(minValue, value);
        this.value = Math.min(maxValue, this.value);
        
        if (getParent() != null)
            raiseEvent(new GuiControlChangedEvent(this));
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        if (grabbedSlider) {
            int width = (int) rect.getWidth() - getContentOffset() * 2 - sliderWidth;
            
            if (x < getContentOffset())
                this.value = this.minValue;
            else if (x > getContentOffset() + width + sliderWidth / 2f)
                this.value = this.maxValue;
            else {
                int mouseOffsetX = (int) (x - getContentOffset() - sliderWidth / 2);
                this.value = (float) (this.minValue + (float) ((this.maxValue - this.minValue) * ((float) mouseOffsetX / (float) width)));
            }
            setValue(value);
        }
    }
    
    @Override
    public void looseFocus() {
        if (textfield != null)
            closeTextField();
        super.looseFocus();
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        if (this.grabbedSlider)
            grabbedSlider = false;
    }
    
    @Override
    public boolean isContainer() {
        return getParent().isContainer();
    }
    
    @Override
    public void closeTopLayer() {
        getParent().closeTopLayer();
    }
    
    @Override
    public Rect toLayerRect(GuiControl control, Rect rect) {
        return getParent().toLayerRect(this, rect);
    }
    
    @Override
    public Rect toScreenRect(GuiControl control, Rect rect) {
        return getParent().toScreenRect(this, rect);
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.NESTED_NO_PADDING;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        double percent = getPercentage();
        
        int posX = (int) ((control.getContentWidth() - sliderWidth) * percent);
        PoseStack pose = graphics.pose();
        GuiStyle style = getStyle();
        style.get(ControlStyleFace.CLICKABLE, false).render(pose, posX, 0, sliderWidth, control.getContentHeight());
        
        if (textfield != null)
            textfield.render(graphics, control, rect, rect, 1, mouseX, mouseY);
        else
            GuiRenderHelper.drawStringCentered(pose, getTextByValue(), control.getContentWidth(), control.getContentHeight(), ColorUtils.WHITE, true);
    }
    
    @Override
    public void flowX(int width, int preferred) {
        if (textfield != null)
            textfield.flowX(width, preferred);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        if (textfield != null)
            textfield.flowY(width, height, preferred);
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 40;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 10;
    }
    
    @Override
    public void closeLayer(GuiLayer layer) {
        getParent().closeLayer(layer);;
    }
    
}
