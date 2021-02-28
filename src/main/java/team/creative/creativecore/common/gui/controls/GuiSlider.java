package team.creative.creativecore.common.gui.controls;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.SoundEvents;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.sync.LayerOpenPacket;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiSlider extends GuiControlBasic implements IGuiParent {
    
    public double maxValue;
    public double minValue;
    public double value;
    protected boolean grabbedSlider;
    public int sliderWidth = 4;
    
    protected GuiTextfield textfield;
    
    public GuiSlider(String name, int x, int y, int width, int height, double value, double min, double max) {
        super(name, x, y, width, height);
        this.minValue = min;
        this.maxValue = max;
        setValue(value);
    }
    
    public String getTextByValue() {
        return Math.round(value * 100F) / 100F + "";
    }
    
    public String getTextfieldValue() {
        return getTextByValue();
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
            textfield.setParent(this);
            return true;
        }
        return false;
    }
    
    protected GuiTextfield createTextfield() {
        return new GuiTextfield(getNestedName() + ".text", 0, 0, getWidth() - getContentOffset() * 2, getHeight() - getContentOffset() * 2).setFloatOnly();
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
    public void mouseMoved(double x, double y) {
        if (grabbedSlider) {
            int width = this.getWidth() - getContentOffset() * 2 - sliderWidth;
            
            if (x < this.getX() + getContentOffset())
                this.value = this.minValue;
            else if (x > this.getX() + getContentOffset() + width + sliderWidth / 2)
                this.value = this.maxValue;
            else {
                int mouseOffsetX = (int) (x - this.getX() - getContentOffset() - sliderWidth / 2);
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
    public void mouseReleased(double x, double y, int button) {
        if (this.grabbedSlider)
            grabbedSlider = false;
    }
    
    @Override
    public boolean isContainer() {
        return getParent().isContainer();
    }
    
    @Override
    public void moveBehind(GuiControl toMove, GuiControl reference) {}
    
    @Override
    public void moveInFront(GuiControl toMove, GuiControl reference) {}
    
    @Override
    public void moveTop(GuiControl toMove) {}
    
    @Override
    public void moveBottom(GuiControl toMove) {}
    
    @Override
    public GuiLayer openLayer(LayerOpenPacket packet) {
        return getParent().openLayer(packet);
    }
    
    @Override
    public void closeTopLayer() {
        getParent().closeTopLayer();
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.SLIDER;
    }
    
    @Override
    protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
        double percent = getPercentage();
        
        int posX = (int) ((getContentWidth() - sliderWidth) * percent);
        
        GuiStyle style = getStyle();
        style.get(ControlStyleFace.CLICKABLE, false).render(matrix, posX, 0, sliderWidth, rect.getHeight());
        
        if (textfield != null)
            textfield.render(matrix, rect, rect, mouseX, mouseY);
        else
            GuiRenderHelper.drawStringCentered(matrix, getTextByValue(), (float) rect.getWidth(), (float) rect.getHeight(), ColorUtils.WHITE, true);
    }
    
}
