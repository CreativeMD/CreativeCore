package team.creative.creativecore.common.gui.control.parent;

import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiLabeledControl extends GuiParent {
    
    public final GuiLabel label;
    
    public GuiLabeledControl(IGuiParent parent, Component text, GuiControl control) {
        super(parent, GuiFlow.STACK_X);
        setVAlign(VAlign.CENTER);
        label = new GuiLabel(parent, "label");
        if (text != null)
            label.setTitle(text);
        add(label);
        add(control);
    }
    
    public GuiLabeledControl(IGuiParent parent, String translate, GuiControl control) {
        this(parent, GuiControl.translatable(translate), control);
    }
    
    public GuiLabeledControl(IGuiParent parent, GuiControl control) {
        this(parent, (Component) null, control);
    }
    
    public <C extends GuiControl> C getInnerControl() {
        return (C) get(1);
    }
    
    public GuiLabeledControl setTranslate(String translate) {
        label.setTranslate(translate);
        return this;
    }
    
    public GuiLabeledControl setTranslate(String translate, Object... params) {
        label.setTranslate(translate, params);
        return this;
    }
    
    public GuiLabeledControl setTitle(Component component) {
        label.setTitle(component);
        return this;
    }
    
    public GuiLabeledControl setTitle(List<Component> components) {
        label.setTitle(components);
        return this;
    }
    
    @Override
    public GuiLabeledControl setTooltip(List<Component> tooltip) {
        label.setTooltip(tooltip);
        return this;
    }
    
    @Override
    public GuiLabeledControl setTooltip(String text) {
        label.setTooltip(text);
        return this;
    }
}
