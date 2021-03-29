package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigSubControl extends GuiParent {
    
    public GuiTextfield nameField;
    public GuiLabel nameLabel;
    
    public GuiConfigSubControl(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        this.borderWidth = this.marginWidth = 0;
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
    public boolean hasBorder() {
        return false;
    }
    
    @Override
    public boolean hasBackground() {
        return false;
    }
    
    @Override
    public boolean hasMouseOverEffect() {
        return false;
    }
    
}
