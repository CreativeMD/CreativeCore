package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiTextfield extends GuiControl {
    
    public GuiTextfield(IGuiParent parent, String name) {
        super(parent, name);
        this.setTextSilent("");
    }
    
    public GuiTextfield(IGuiParent parent, String name, String text) {
        super(parent, name);
        this.setTextSilent(text);
    }
    
    public GuiTextfield(IGuiParent parent, String name, String text, int maxStringLength) {
        super(parent, name);
        if (dist() != null)
            dist().setMaxStringLength(maxStringLength);
        this.setTextSilent(text);
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
        if (dist() != null)
            dist().setFloatOnly();
        return this;
    }
    
    public GuiTextfield setNumbersIncludingNegativeOnly() {
        if (dist() != null)
            dist().setNumbersIncludingNegativeOnly();
        return this;
    }
    
    public GuiTextfield setNumbersOnly() {
        if (dist() != null)
            dist().setNumbersOnly();
        return this;
    }
    
    public float parseFloat() {
        try {
            if (dist() != null)
                return Float.parseFloat(dist().getText());
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public double parseDouble() {
        try {
            if (dist() != null)
                return Double.parseDouble(dist().getText());
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public int parseInteger() {
        try {
            if (dist() != null)
                return Integer.parseInt(dist().getText());
            return 0;
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
        if (dist() != null)
            dist().tick();
    }
    
    protected void setTextSilent(String textIn) {
        if (dist() != null)
            dist().setText(textIn, false);
    }
    
    public GuiTextfield setText(String textIn) {
        if (dist() != null)
            dist().setText(textIn, true);
        return this;
    }
    
    public String getText() {
        if (dist() != null)
            return dist().getText();
        return "";
    }
    
    public void setValidator(Predicate<String> validatorIn) {
        if (dist() != null)
            dist().setValidator(validatorIn);
    }
    
    public GuiTextfield setMaxStringLength(int length) {
        if (dist() != null)
            dist().setMaxStringLength(length);
        return this;
    }
    
    public GuiTextfield setSuggestion(@Nullable String suggestion) {
        if (dist() != null)
            dist().setSuggestion(suggestion);
        return this;
    }
    
    public void setCursorPositionZero() {
        if (dist() != null)
            dist().setCursorPositionZero();
    }
    
    public static interface GuiTextfieldDist extends GuiFocusControlDist {
        
        public void setFloatOnly();
        
        public void setNumbersIncludingNegativeOnly();
        
        public void setNumbersOnly();
        
        public void tick();
        
        public void setText(String textIn, boolean notify);
        
        public String getText();
        
        public void setValidator(Predicate<String> validatorIn);
        
        public void setSuggestion(@Nullable String suggestion);
        
        public void setMaxStringLength(int length);
        
        public void setCursorPositionZero();
        
    }
}
