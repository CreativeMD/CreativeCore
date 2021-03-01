package team.creative.creativecore.common.gui.controls;

import net.minecraft.client.gui.screen.Screen;

public class GuiSteppedSlider extends GuiSlider {
    
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
    public boolean mouseScrolled(double x, double y, double scrolled) {
        setValue(value + (Screen.hasShiftDown() ? 10 : 1) * (scrolled > 0 ? 1 : -1));
        return true;
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
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
