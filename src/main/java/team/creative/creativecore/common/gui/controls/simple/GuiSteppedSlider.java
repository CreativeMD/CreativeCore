package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.client.gui.screens.Screen;
import team.creative.creativecore.common.gui.parser.DoubleValueParser;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiSteppedSlider extends GuiSlider {
    
    public GuiSteppedSlider(String name, int value, int min, int max) {
        super(name, value, min, max);
    }
    
    public GuiSteppedSlider(String name, int value, int min, int max, DoubleValueParser parser) {
        super(name, value, min, max, parser);
    }
    
    @Override
    public String getTextByValue() {
        return ((int) value) + "";
    }
    
    @Override
    public String getTextfieldValue() {
        return ((int) value) + "";
    }
    
    public void stepUp() {
        setValue(getValue() + 1);
    }
    
    public void stepDown() {
        setValue(getValue() - 1);
    }
    
    @Override
    public boolean mouseScrolled(Rect rect, double x, double y, double scrolled) {
        setValue(value + (Screen.hasShiftDown() ? 10 : 1) * (scrolled > 0 ? 1 : -1));
        return true;
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        super.mouseMoved(rect, x, y);
        value = (int) value;
    }
    
    @Override
    public void setValue(double value) {
        super.setValue((int) value);
    }
    
    public int getValue() {
        return (int) value;
    }
    
    @Override
    protected GuiTextfield createTextfield(Rect rect) {
        return super.createTextfield(rect).setNumbersIncludingNegativeOnly();
    }
    
}
