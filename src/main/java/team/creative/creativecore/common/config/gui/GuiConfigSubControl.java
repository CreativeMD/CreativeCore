package team.creative.creativecore.common.config.gui;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiConfigSubControl extends GuiParent {
    
    public GuiTextfield nameField;
    public GuiLabel nameLabel;
    
    public GuiConfigSubControl(String name) {
        super(name);
        setExpandable();
    }
    
    public String getName() {
        if (nameLabel != null)
            return nameLabel.getCaption();
        if (nameField != null)
            return nameField.text;
        return "";
    }
    
    public void addNameUnmodifieable(String name) {
        nameLabel = new GuiLabel("label", 0, 0);
        nameLabel.setCaption(name);
        int offsetY = nameLabel.height + 2;
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).posY += offsetY;
        height += offsetY;
        addControl(nameLabel);
    }
    
    public void addNameTextfield(String name) {
        nameField = new GuiTextfield(name, 0, 0, width - 50);
        int offsetY = nameField.height + 2;
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).posY += offsetY;
        height += offsetY;
        addControl(nameField);
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public void setWidthLayout(int width) {
        setWidth(width);
    }
    
    @Override
    public int getMinWidth() {
        return 20;
    }
    
    @Override
    public int getPreferredWidth() {
        return initalWidth;
    }
    
    @Override
    public void setHeightLayout(int height) {
        setHeight(height);
    }
    
    @Override
    public int getMinHeight() {
        return 10;
    }
    
    @Override
    public int getPreferredHeight() {
        return initalHeight;
    }
}
