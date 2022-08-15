package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.util.Mth;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiCounter extends GuiParent {
    
    public int min;
    public int max;
    public GuiTextfield textfield;
    
    public GuiCounter(String name, int value, int min, int max) {
        super(name);
        this.min = min;
        this.max = max;
        flow = GuiFlow.STACK_X;
        valign = VAlign.STRETCH;
        spacing = 0;
        textfield = new GuiTextfield("value", "" + Mth.clamp(value, min, max), 20, 8).setNumbersOnly();
        add(textfield.setExpandableX());
        GuiParent buttons = new GuiParent(GuiFlow.STACK_Y);
        add(buttons);
        buttons.add(new GuiButton("+", x -> {
            textfield.setText("" + stepUp(textfield.parseInteger()));
            raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
        }).setTranslate("gui.plus"));
        buttons.add(new GuiButton("-", x -> {
            textfield.setText("" + stepDown(textfield.parseInteger()));
            raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
        }).setTranslate("gui.minus"));
        
    }
    
    @Override
    public boolean isExpandableX() {
        return expandableX;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
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