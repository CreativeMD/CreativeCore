package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiTextfield extends GuiControl {
    
    public GuiTextfield(IGuiParent parent, String name) {
        super(parent, name);
        this.setText("");
    }
    
    public GuiTextfield(IGuiParent parent, String name, String text) {
        super(parent, name);
        this.setText(text);
    }
    
    public GuiTextfield(IGuiParent parent, String name, String text, int maxStringLength) {
        super(parent, name);
        dist().setMaxStringLength(maxStringLength);
        this.setText(text);
    }
    
    @Override
    public GuiTextfieldDist dist() {
        return (GuiTextfieldDist) super.dist();
    }
    
    @Override
    public GuiTextfield setDim(int width, int height) {
        return (GuiTextfield) super.setDim(width, height);
    }
    
    public GuiTextfield setDim(int width) {
        return (GuiTextfield) super.setDim(width, 10);
    }
    
    public GuiTextfield setFloatOnly() {
        dist().setFloatOnly();
        return this;
    }
    
    public GuiTextfield setNumbersIncludingNegativeOnly() {
        dist().setNumbersIncludingNegativeOnly();
        return this;
    }
    
    public GuiTextfield setNumbersOnly() {
        dist().setNumbersOnly();
        return this;
    }
    
    public float parseFloat() {
        try {
            return Float.parseFloat(dist().getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public double parseDouble() {
        try {
            return Double.parseDouble(dist().getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public int parseInteger() {
        try {
            return Integer.parseInt(dist().getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {
        dist().tick();
    }
    
    public GuiTextfield setText(String textIn) {
        dist().setText(textIn);
        return this;
    }
    
    public String getText() {
        return dist().getText();
    }
    
    public void setValidator(Predicate<String> validatorIn) {
        dist().setValidator(validatorIn);
    }
    
    public GuiTextfield setMaxStringLength(int length) {
        dist().setMaxStringLength(length);
        return this;
    }
    
    public GuiTextfield setSuggestion(@Nullable String suggestion) {
        dist().setSuggestion(suggestion);
        return this;
    }
    
    public void setCursorPositionZero() {
        dist().setCursorPositionZero();
    }
    
    public static interface GuiTextfieldDist extends GuiFocusControlDist {
        
        public void setFloatOnly();
        
        public void setNumbersIncludingNegativeOnly();
        
        public void setNumbersOnly();
        
        public void tick();
        
        public void setText(String textIn);
        
        public String getText();
        
        public void setValidator(Predicate<String> validatorIn);
        
        public void setSuggestion(@Nullable String suggestion);
        
        public void setMaxStringLength(int length);
        
        public void setCursorPositionZero();
        
    }
}
