package team.creative.creativecore.common.gui.control.simple;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiSliderUpdateEvent;
import team.creative.creativecore.common.gui.parser.DoubleValueParser;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiSlider extends GuiControl implements IGuiParent {
    
    protected double maxValue;
    protected double minValue;
    protected double value;
    private final DoubleValueParser parser;
    protected boolean grabbedSlider;
    public int sliderSize = 4;
    
    protected GuiTextfield textfield;
    private GuiSlider minSlider;
    private GuiSlider maxSlider;
    
    public GuiSlider(String name, double value, double min, double max) {
        this(name, value, min, max, DoubleValueParser.NONE);
    }
    
    public GuiSlider(String name, double value, double min, double max, DoubleValueParser parser) {
        super(name);
        this.minValue = min;
        this.maxValue = max;
        this.parser = parser;
        setValue(value);
    }
    
    public String getTextByValue() {
        return parser.parse(getValue(), getMaxValue());
    }
    
    public String getTextfieldValue() {
        return this.getTextByValue();
    }
    
    public double getPercentage() {
        return (this.value - this.minValue) / (this.maxValue - this.minValue);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            if (textfield != null)
                return textfield.mouseClicked(x, y, button);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            grabbedSlider = true;
            mouseMoved(x, y);
            return true;
        } else if (button == 1) {
            grabbedSlider = false;
            textfield = createTextfield();
            textfield.focus();
            textfield.setText(getTextfieldValue());
            textfield.setCursorPositionEnd();
            textfield.setParent(this);
            int width = rect.getWidth();
            textfield.flowX(width, width);
            return true;
        }
        return false;
    }
    
    protected GuiTextfield createTextfield() {
        return new GuiTextfield(getNestedName() + ".text").setFloatOnly().setDim(rect.getContentWidth(), rect.getContentHeight());
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
    
    public void setMaxValue(double maxValue) {
        if (this.maxValue != maxValue) {
            this.maxValue = Math.max(this.minValue, maxValue);
            this.setValue(value);
        }
    }
    
    public void setMinValue(double minValue) {
        if (this.minValue != minValue) {
            this.minValue = Math.min(minValue, this.maxValue);
            this.setValue(value);
        }
    }
    
    public void setValue(double value) {
        this.value = Math.max(minValue, value);
        this.value = Math.min(maxValue, this.value);
        
        if (this.getParent() != null)
            this.raiseEvent(new GuiSliderUpdateEvent(this));
        if (this.minSlider != null) {
            this.minSlider.setMaxValue(value);
        }
        if (this.maxSlider != null) {
            this.maxSlider.setMinValue(value);
        }
    }
    
    public double getValue() {
        return value;
    }
    
    public double getMinValue() {
        return minValue;
    }
    
    public double getMaxValue() {
        return maxValue;
    }
    
    public GuiSlider setSliderSize(int size) {
        this.sliderSize = size;
        return this;
    }
    
    public GuiSlider setMinSlider(GuiSlider slider) {
        if (slider == this)
            throw new IllegalArgumentException("slider argument is current slider");
        this.minSlider = slider;
        return this;
    }
    
    public GuiSlider setMaxSlider(GuiSlider slider) {
        if (slider == this)
            throw new IllegalArgumentException("slider argument is current slider");
        this.maxSlider = slider;
        return this;
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
            this.setValue(value);
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
    public boolean isContainer() {
        return this.getParent().isContainer();
    }
    
    @Override
    public void closeTopLayer() {
        this.getParent().closeTopLayer();
    }
    
    @Override
    public Rect toLayerRect(GuiControl control, Rect rect) {
        return this.getParent().toLayerRect(this, rect);
    }
    
    @Override
    public Rect toScreenRect(GuiControl control, Rect rect) {
        return this.getParent().toScreenRect(this, rect);
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
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        double percent = getPercentage();
        
        int posX = (int) ((rect.getContentWidth() - sliderSize) * percent);
        GuiStyle style = getStyle();
        style.get(ControlStyleFace.CLICKABLE, false).render(graphics, posX, 0, sliderSize, rect.getContentHeight());
        
        if (textfield != null)
            textfield.render(graphics, controlRect, controlRect, 1, mouseX, mouseY);
        else
            ((CreativeGuiGraphics) graphics).drawStringCentered(getTextByValue(), rect.getContentWidth(), rect.getContentHeight(), ColorUtils.WHITE, true);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {}
    
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
        this.getParent().closeLayer(layer);;
    }
    
    @Override
    public GuiControl setEnabled(boolean enabled) {
        if (!enabled)
            grabbedSlider = false;
        return super.setEnabled(enabled);
    }
    
}
