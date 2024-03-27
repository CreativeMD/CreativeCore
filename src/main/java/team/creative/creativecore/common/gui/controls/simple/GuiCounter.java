package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.util.Mth;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiCounter extends GuiParent {
    
    public int min;
    public int max;
    public final GuiParent buttons = new GuiParent(GuiFlow.STACK_Y);
    public GuiTextfield textfield;
    public final ControlFormatting buttonsFormatting;
    
    public GuiCounter(String name, int value) {
        this(name, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    public GuiCounter(String name, int value, int min, int max) {
        this(name, value, min, max, ControlFormatting.TRANSPARENT);
    }

    public GuiCounter(String name, int value, int min, int max, ControlFormatting buttonsFormatting) {
        super(name);
        this.min = min;
        this.max = max;
        this.flow = GuiFlow.STACK_X;
        this.setSpacing(1);
        this.textfield = new GuiTextfield("value", "" + Mth.clamp(value, min, max)).setDim(20, 10).setNumbersIncludingNegativeOnly();
        this.buttons.spacing = 0;
        this.buttonsFormatting = buttonsFormatting;
        this.createButtons();
        this.add(textfield.setExpandableX());
        this.add(buttons);
    }

    protected void createButtons() {
        this.buttons.add(new GuiButtonHoldSlim("+", x -> {
            this.textfield.setText("" + stepUp(this.textfield.parseInteger()));
            this.raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
        }).setTranslate("gui.plus").setDim(6, 3));
        this.buttons.add(new GuiButtonHoldSlim("-", x -> {
            this.textfield.setText("" + stepDown(this.textfield.parseInteger()));
            this.raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
        }).setTranslate("gui.minus").setDim(6, 4));
    }

    public GuiCounter setSpacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    public GuiCounter addControl(GuiControl control) {
        this.add(control);
        return this;
    }

    public GuiButtonHoldSlim getPlusButton() {
        return this.buttons.get("+");
    }

    public GuiButtonHoldSlim getMinusButton() {
        return this.buttons.get("-");
    }

    @Override
    public boolean isExpandableX() {
        return expandableX;
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