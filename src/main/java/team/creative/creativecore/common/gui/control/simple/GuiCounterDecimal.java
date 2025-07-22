package team.creative.creativecore.common.gui.control.simple;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.minecraft.util.Mth;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiCounterDecimal extends GuiParent {
    
    public static final DecimalFormat FORMAT = new DecimalFormat("0.##");
    
    static {
        FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }
    
    public double min;
    public double max;
    public final GuiParent buttons;
    public GuiTextfield textfield;
    public double stepAmount;
    public final ControlFormatting buttonsFormatting;
    
    public GuiCounterDecimal(IGuiParent parent, String name, double value) {
        this(parent, name, value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    public GuiCounterDecimal(IGuiParent parent, String name, double value, double min, double max) {
        this(parent, name, value, min, max, ControlFormatting.TRANSPARENT);
    }
    
    public GuiCounterDecimal(IGuiParent parent, String name, double value, double min, double max, ControlFormatting buttonsFormatting) {
        super(parent, name);
        this.min = min;
        this.max = max;
        this.stepAmount = 1;
        this.setSpacing(1);
        this.setFlow(GuiFlow.STACK_X);
        this.textfield = new GuiTextfield(parent, "value", "" + Mth.clamp(value, min, max)).setDim(30, 10).setFloatOnly();
        this.buttons = new GuiParent(parent, GuiFlow.STACK_Y);
        this.buttons.setSpacing(0);
        this.buttonsFormatting = buttonsFormatting;
        this.createButtons();
        this.add(textfield.setExpandableX());
        this.add(buttons);
    }
    
    protected void createButtons() {
        this.buttons.add(new GuiButtonHold(getParent(), "+", x -> stepUp()).setHoverEffect(true).setTranslate("gui.plus").setFormatting(buttonsFormatting).setDim(6, 3));
        this.buttons.add(new GuiButtonHold(getParent(), "-", x -> stepDown()).setHoverEffect(true).setTranslate("gui.minus").setFormatting(buttonsFormatting).setDim(6, 3));
    }
    
    @Override
    public GuiCounterDecimal setSpacing(int spacing) {
        return (GuiCounterDecimal) super.setSpacing(spacing);
    }
    
    @Override
    public GuiCounterDecimal add(GuiControl control) {
        return (GuiCounterDecimal) super.add(control);
    }
    
    @Override
    public boolean isExpandableX() {
        return dist().isExpandableX();
    }
    
    public GuiButtonHold getPlusButton() {
        return this.buttons.get("+");
    }
    
    public GuiButtonHold getMinusButton() {
        return this.buttons.get("-");
    }
    
    public GuiCounterDecimal setStep(double amount) {
        this.stepAmount = amount;
        return this;
    }
    
    public DecimalFormat getFormat() {
        return FORMAT;
    }
    
    public void resetTextfield() {
        textfield.setCursorPositionZero();
    }
    
    @Override
    public void raiseEvent(GuiEvent event) {
        if (event instanceof GuiControlChangedEvent controlEvent && controlEvent.control.is("value"))
            super.raiseEvent(new GuiControlChangedEvent(GuiCounterDecimal.this));
        else
            super.raiseEvent(event);
    }
    
    public void stepUp() {
        setValue(getValue() + stepAmount);
    }
    
    public void stepDown() {
        setValue(getValue() - stepAmount);
    }
    
    public double getValue() {
        return Mth.clamp(textfield.parseDouble(), min, max);
    }
    
    public void setValue(double value) {
        textfield.setText(getFormat().format(Mth.clamp(value, min, max)));
        raiseEvent(new GuiControlChangedEvent(GuiCounterDecimal.this));
    }
    
}