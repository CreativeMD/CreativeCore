package team.creative.creativecore.common.config.gui;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiConfigSubControl extends GuiParent {
    
    public GuiTextfield nameField;
    public GuiLabel nameLabel;
    public boolean defaultHolder;
    
    public GuiConfigSubControl(IGuiParent parent, String name) {
        super(parent, name, GuiFlow.STACK_X);
        setExpandableX();
        setFormatting(ControlFormatting.NESTED);
    }
    
    public String getName() {
        if (nameLabel != null)
            return nameLabel.name;
        if (nameField != null)
            return nameField.getText();
        return "";
    }
    
    public void addNameUnmodifieable(String name) {
        nameLabel = new GuiLabel(this, "title").setTitle(Component.literal(name));
        add(nameLabel);
    }
    
    public void addNameTextfield(String name) {
        add(nameField = new GuiTextfield(this, "title", name).setDim(50, 8));
    }
    
}
