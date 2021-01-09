package com.creativemd.creativecore.common.gui.controls.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiSteppedSlider extends GuiAnalogeSlider {
    
    public GuiSteppedSlider(String name, int x, int y, int width, int height, int value, int min, int max) {
        super(name, x, y, width, height, value, min, max);
    }
    
    @Override
    public String getTextByValue() {
        return ((int) value) + "";
    }
    
    @Override
    public String getTextfieldValue() {
        return ((int) value) + "";
    }
    
    @Override
    public boolean mouseScrolled(int x, int y, int scrolled) {
        setValue(value + (GuiScreen.isCtrlKeyDown() ? 10 : 1) * (scrolled > 0 ? 1 : -1));
        return true;
    }
    
    @Override
    public void mouseMove(int posX, int posY, int button) {
        super.mouseMove(posX, posY, button);
        value = (int) value;
    }
    
    @Override
    public void setValue(double value) {
        super.setValue((int) value);
    }
    
    @Override
    protected GuiTextfield createTextfield() {
        return super.createTextfield().setNumbersIncludingNegativeOnly();
    }
}
