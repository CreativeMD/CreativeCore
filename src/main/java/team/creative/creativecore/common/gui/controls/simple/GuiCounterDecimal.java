package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.util.Mth;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiCounterDecimal extends GuiParent {
    
    public float min;
    public float max;
    public GuiTextfield textfield;
    
    public GuiCounterDecimal(String name, float value) {
        this(name, value, Float.MIN_VALUE, Float.MAX_VALUE);
    }
    
    public GuiCounterDecimal(String name, float value, float min, float max) {
        super(name);
        this.min = min;
        this.max = max;
        flow = GuiFlow.STACK_X;
        spacing = 1;
        textfield = new GuiTextfield("value", "" + Mth.clamp(value, min, max)).setDim(20, 10).setFloatOnly();
        add(textfield.setExpandableX());
        GuiParent buttons = new GuiParent(GuiFlow.STACK_Y);
        buttons.spacing = 0;
        add(buttons);
        buttons.add(new GuiButtonHoldSlim("+", x -> {
            textfield.setText("" + stepUp(textfield.parseFloat()));
            raiseEvent(new GuiControlChangedEvent(GuiCounterDecimal.this));
        }).setTranslate("gui.plus").setDim(6, 3));
        buttons.add(new GuiButtonHoldSlim("-", x -> {
            textfield.setText("" + stepDown(textfield.parseFloat()));
            raiseEvent(new GuiControlChangedEvent(GuiCounterDecimal.this));
        }).setTranslate("gui.minus").setDim(6, 3));
        
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
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
    
    @Override
    public boolean isExpandableX() {
        return expandableX;
    }
    
    public float stepUp(float value) {
        return Math.min(max, value + 1);
    }
    
    public float stepDown(float value) {
        return Math.max(min, value - 1);
    }
    
    public float getValue() {
        return Mth.clamp(textfield.parseFloat(), min, max);
    }
    
    public void setValue(float value) {
        textfield.setText("" + Mth.clamp(value, min, max));
    }
    
}