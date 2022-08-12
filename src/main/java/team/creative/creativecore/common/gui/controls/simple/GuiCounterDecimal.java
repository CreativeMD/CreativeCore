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
    
    public GuiCounterDecimal(String name, float value, float min, float max) {
        super(name);
        this.min = min;
        this.max = max;
        flow = GuiFlow.STACK_X;
        spacing = 0;
        textfield = new GuiTextfield("value", "" + Mth.clamp(value, min, max), 20, 8).setNumbersOnly();
        add(textfield);
        GuiParent buttons = new GuiParent(GuiFlow.STACK_Y);
        add(buttons);
        buttons.add(new GuiButton("-", x -> {
            textfield.setText("" + stepDown(textfield.parseFloat()));
            raiseEvent(new GuiControlChangedEvent(GuiCounterDecimal.this));
        }).setTranslate("gui.minus"));
        buttons.add(new GuiButton("+", x -> {
            textfield.setText("" + stepUp(textfield.parseFloat()));
            raiseEvent(new GuiControlChangedEvent(GuiCounterDecimal.this));
        }).setTranslate("gui.plus"));
        
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public void raiseEvent(GuiEvent event) {
        if (event instanceof GuiControlChangedEvent controlEvent && controlEvent.control.is("value"))
            super.raiseEvent(new GuiControlChangedEvent(GuiCounterDecimal.this));
        else
            super.raiseEvent(event);
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