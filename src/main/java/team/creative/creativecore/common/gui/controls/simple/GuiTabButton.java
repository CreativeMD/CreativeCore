package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.util.text.ITextCollection;
import team.creative.creativecore.common.util.text.TextListBuilder;

public class GuiTabButton extends GuiParent {
    
    public static final ControlFormatting BUTTON_ACTIVE = new ControlFormatting(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE);
    public static final ControlFormatting BUTTON_INACTIVE = new ControlFormatting(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE_INACTIVE);
    
    private int index = 0;
    public GuiBorderlessButton selected;
    
    public GuiTabButton(String name, ITextCollection states) {
        this(name, 0, states);
    }
    
    public GuiTabButton(String name, int index, ITextCollection states) {
        super(name, null);
        flow = GuiFlow.STACK_X;
        int i = 0;
        for (CompiledText text : states.build()) {
            add(new GuiBorderlessButton("b" + i, null).setText(text));
            i++;
        }
        select(index);
    }
    
    public GuiTabButton(String name, int index, String... states) {
        this(name, index, new TextListBuilder().add(states));
    }
    
    public void select(int index) {
        this.index = index;
        if (selected != null)
            selected.active = false;
        selected = (GuiBorderlessButton) controls.get(index).control;
        selected.active = true;
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public int index() {
        return index;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    public static class GuiBorderlessButton extends GuiButton {
        
        public boolean active = false;
        
        public GuiBorderlessButton(String name, Consumer<Integer> pressed) {
            super(name, pressed);
        }
        
        @Override
        public ControlFormatting getControlFormatting() {
            if (active)
                return BUTTON_ACTIVE;
            return BUTTON_INACTIVE;
        }
    }
    
}
