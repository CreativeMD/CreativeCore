package team.creative.creativecore.common.gui.controls.parent;

import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiLabeledControl extends GuiParent {
    
    public final GuiLabel label;
    
    public GuiLabeledControl(Component text, GuiControl control) {
        flow = GuiFlow.STACK_X;
        valign = VAlign.CENTER;
        label = new GuiLabel("label");
        if (text != null)
            label.setTitle(text);
        add(label);
        add(control);
    }
    
    public GuiLabeledControl(String translate, GuiControl control) {
        this(GuiControl.translatable(translate), control);
    }
    
    public GuiLabeledControl(GuiControl control) {
        this((Component) null, control);
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
