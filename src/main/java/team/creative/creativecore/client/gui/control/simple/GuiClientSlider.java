package team.creative.creativecore.client.gui.control.simple;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.control.simple.GuiSlider;
import team.creative.creativecore.common.gui.control.simple.GuiSlider.GuiSliderDist;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.event.GuiSliderUpdateEvent;
import team.creative.creativecore.common.gui.parser.DoubleValueParser;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiClientSlider<T extends GuiSlider> extends GuiClientControl<T> implements GuiSliderDist {
    
    protected double maxValue;
    protected double minValue;
    protected double value;
    private DoubleValueParser parser;
    protected boolean grabbedSlider;
    public int sliderSize = 4;
    
    protected GuiTextfield textfield;
    private GuiSlider minSlider;
    private GuiSlider maxSlider;
    
    public GuiClientSlider(T control) {
        super(control);
    }
    
    @Override
    public String getTextByValue() {
        return parser.parse(getValue(), getMaxValue());
    }
    
    @Override
    public String getTextfieldValue() {
        return this.getTextByValue();
    }
    
    @Override
    public double getPercentage() {
        return (this.value - this.minValue) / (this.maxValue - this.minValue);
    }
    
    @Override
    public void setParser(DoubleValueParser parser) {
        this.parser = parser;
    }
    
    @Override
    public void setSliderSize(int size) {
        this.sliderSize = size;
    }
    
    @Override
    public void setMinSlider(GuiSlider slider) {
        if (slider == control)
            throw new IllegalArgumentException("slider argument is current slider");
        this.minSlider = slider;
    }
    
    @Override
    public void setMaxSlider(GuiSlider slider) {
        if (slider == control)
            throw new IllegalArgumentException("slider argument is current slider");
        this.maxSlider = slider;
    }
    
    protected GuiClientTextfield textfield() {
        return (GuiClientTextfield) textfield.dist();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled)
            grabbedSlider = false;
        super.setEnabled(enabled);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            if (textfield != null)
                return textfield().mouseClicked(x, y, button);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            grabbedSlider = true;
            mouseMoved(x, y);
            return true;
        } else if (button == 1) {
            grabbedSlider = false;
            textfield = createTextfield();
            textfield().focus();
            textfield.setText(getTextfieldValue());
            textfield().setCursorPositionEnd();
            GuiControl.setParent(textfield, control);
            int width = rect.getWidth();
            textfield().rect.setWidth(width, width);
            textfield().rect.flowX();
            int height = rect.getHeight();
            textfield().rect.setHeight(height, height);
            textfield().rect.flowY();
            return true;
        }
        return false;
    }
    
    protected GuiTextfield createTextfield() {
        return new GuiTextfield(control, control.getNestedName() + ".text").setFloatOnly().setDim(rect.getContentWidth(), rect.getContentHeight());
    }
    
    @Override
    public void closeTextField() {
        double value = this.value;
        try {
            setValue(textfield.parseFloat(), true);
            playSound(SoundEvents.UI_BUTTON_CLICK);
        } catch (NumberFormatException e) {
            setValue(value, true);
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
            return textfield().keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (textfield != null)
            return textfield().charTyped(codePoint, modifiers);
        return super.charTyped(codePoint, modifiers);
    }
    
    @Override
    public void setBounds(double min, double max, boolean notify) {
        this.minValue = min;
        this.maxValue = max;
        setValue(Math.clamp(value, min, max), notify);
    }
    
    @Override
    public void setMaxValue(double maxValue) {
        if (this.maxValue != maxValue) {
            this.maxValue = Math.max(this.minValue, maxValue);
            this.setValue(value, true);
        }
    }
    
    @Override
    public void setMinValue(double minValue) {
        if (this.minValue != minValue) {
            this.minValue = Math.min(minValue, this.maxValue);
            this.setValue(value, true);
        }
    }
    
    @Override
    public void setValue(double value, boolean notify) {
        double valueBefore = this.value;
        this.value = Math.max(minValue, value);
        this.value = Math.min(maxValue, this.value);
        
        if (notify && valueBefore != this.value)
            this.raiseEvent(new GuiSliderUpdateEvent(control));
        if (this.minSlider != null)
            this.minSlider.setMaxValue(value);
        if (this.maxSlider != null)
            this.maxSlider.setMinValue(value);
    }
    
    @Override
    public double getValue() {
        return value;
    }
    
    @Override
    public double getMinValue() {
        return minValue;
    }
    
    @Override
    public double getMaxValue() {
        return maxValue;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.NESTED_NO_PADDING;
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        if (grabbedSlider) {
            int width = rect.getContentWidth() - sliderSize;
            
            if (x < getContentOffset())
                this.value = this.minValue;
            else if (x > getContentOffset() + width + sliderSize / 2f)
                this.value = this.maxValue;
            else {
                int mouseOffsetX = (int) (x - getContentOffset() - sliderSize / 2);
                this.value = (float) (this.minValue + (float) ((this.maxValue - this.minValue) * ((float) mouseOffsetX / (float) width)));
            }
            this.setValue(value, true);
        }
    }
    
    @Override
    public void looseFocus() {
        if (textfield != null)
            this.closeTextField();
        super.looseFocus();
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        if (this.grabbedSlider)
            this.grabbedSlider = false;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        double percent = getPercentage();
        
        int posX = (int) ((rect.getContentWidth() - sliderSize) * percent);
        GuiStyle style = getStyle();
        style.get(ControlStyleFace.CLICKABLE, false).render(graphics, posX, 0, sliderSize, rect.getContentHeight());
        
        if (textfield != null)
            ((GuiClientControl) textfield.dist()).render(graphics, controlRect, controlRect, 1, mouseX, mouseY);
        else
            ((CreativeGuiGraphics) graphics).drawStringCentered(getTextByValue(), rect.getContentWidth(), rect.getContentHeight(), ColorUtils.WHITE, true);
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {}
    
    @Override
    public void flowX(int width, int preferred) {
        if (textfield != null)
            textfield().flowX(width, preferred);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        if (textfield != null)
            textfield().flowY(width, height, preferred);
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 40;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 10;
    }
    
}
