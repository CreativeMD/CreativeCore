package team.creative.creativecore.common.gui.control.simple;

import net.minecraft.util.Mth;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiCounter extends GuiParent {
    
    public int min;
    public int max;
    public final GuiParent buttons;
    public GuiTextfield textfield;
    public final ControlFormatting buttonsFormatting;
    
    public GuiCounter(IGuiParent parent, String name, int value) {
        this(parent, name, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    public GuiCounter(IGuiParent parent, String name, int value, int min, int max) {
        this(parent, name, value, min, max, ControlFormatting.TRANSPARENT);
    }
    
    public GuiCounter(IGuiParent parent, String name, int value, int min, int max, ControlFormatting buttonsFormatting) {
        super(parent, name);
        this.min = min;
        this.max = max;
        dist().setFlow(GuiFlow.STACK_X);
        this.setSpacing(1);
        this.textfield = new GuiTextfield(parent, "value", "" + Mth.clamp(value, min, max)).setDim(20, 10).setNumbersIncludingNegativeOnly();
        this.buttons = new GuiParent(parent, GuiFlow.STACK_Y);
        this.buttons.setSpacing(0);
        this.buttonsFormatting = buttonsFormatting;
        this.createButtons();
        this.add(textfield.setExpandableX());
        this.add(buttons);
    }
    
    protected void createButtons() {
        this.buttons.add(new GuiButtonHold(getParent(), "+", x -> {
            this.textfield.setText("" + stepUp(this.textfield.parseInteger()));
            this.raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
        }).setHoverEffect(true).setTranslate("gui.plus").setDim(6, 3).setFormatting(buttonsFormatting));
        this.buttons.add(new GuiButtonHold(getParent(), "-", x -> {
            this.textfield.setText("" + stepDown(this.textfield.parseInteger()));
            this.raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
        }).setHoverEffect(true).setTranslate("gui.minus").setDim(6, 4).setFormatting(buttonsFormatting));
    }
    
    @Override
    public GuiCounter setSpacing(int spacing) {
        return (GuiCounter) super.setSpacing(spacing);
    }
    
    @Override
    public GuiCounter add(GuiControl control) {
        return (GuiCounter) super.add(control);
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
    
    public void resetTextfield() {
        textfield.setCursorPositionZero();
    }
    
    @Override
    public void raiseEvent(GuiEvent event) {
        if (event instanceof GuiControlChangedEvent controlEvent && controlEvent.control.is("value"))
            super.raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
        else
            super.raiseEvent(event);
    }
    
    public int stepUp(int value) {
        return Math.min(max, value + 1);
    }
    
    public int stepDown(int value) {
        return Math.max(min, value - 1);
    }
    
    public int getValue() {
        return Mth.clamp(textfield.parseInteger(), min, max);
    }
    
    public void setValue(int value) {
        textfield.setText("" + Mth.clamp(value, min, max));
    }
}